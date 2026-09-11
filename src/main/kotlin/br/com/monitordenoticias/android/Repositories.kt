package br.com.monitordenoticias.android

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class NewsRepository(private val db:NewsDb){
    suspend fun searchPeriodProgressive(start:Long,end:Long,sources:List<MediaSource>,allSources:Boolean,onUpdate:(NewsSearchUpdate)->Unit):SearchResult=withContext(Dispatchers.IO){
        val terms=db.listTerms().ifEmpty{DesktopEstablishedTerms.defaults}; val out=linkedMapOf<String,News>(); val errs=mutableListOf<String>(); var processed=0
        val total=(sources.size*terms.size).coerceAtLeast(1); onUpdate(NewsSearchUpdate(LiveSearchProgress(true,System.currentTimeMillis(),total=total)))
        for(src in sources){ for(term in terms){ val q=buildString{append('"').append(term).append('"'); if(!allSources) append(" site:").append(java.net.URI(src.url).host)}; try{ val enc=URLEncoder.encode(q,StandardCharsets.UTF_8); val doc=Jsoup.connect("https://news.google.com/rss/search?q=$enc&hl=pt-BR&gl=BR&ceid=BR:pt-419").userAgent("Mozilla/5.0").timeout(12000).get(); for(item in doc.select("item")){ val title=item.selectFirst("title")?.text()?.substringBeforeLast(" - ")?.trim().orEmpty(); val rawLink=item.selectFirst("link")?.text().orEmpty(); val pub=item.selectFirst("pubDate")?.text(); val ts=runCatching{ZonedDateTime.parse(pub,DateTimeFormatter.RFC_1123_DATE_TIME).toInstant().toEpochMilli()}.getOrNull(); if(title.isBlank()||rawLink.isBlank()||ts==null||ts !in start..end) continue; val n=News(title=title,source=src.name,date=ts,link=rawLink,snippet=item.selectFirst("description")?.text().orEmpty(),important=true,matchedTerm=term); out[UrlPolicy.canonicalizeUrl(rawLink)]=n } }catch(e:Exception){errs+="${src.name}: ${e.message}"}; processed++; onUpdate(NewsSearchUpdate(LiveSearchProgress(true,currentSource=src.name,currentQuery=term,processed=processed,total=total,found=out.size,errors=errs.size),out.values.toList())) } }
        val before=db.links(); val items=out.values.sortedByDescending{it.date?:0}; db.insertNews(items); val newCount=items.count{UrlPolicy.canonicalizeUrl(it.link)!in before}; onUpdate(NewsSearchUpdate(LiveSearchProgress(false,processed=processed,total=total,found=items.size,newItems=newCount,errors=errs.size),items)); SearchResult(items,newCount,errs.size,errs,processed)
    }
    suspend fun searchDemand(d:Demand):DemandSearchResult{ val now=System.currentTimeMillis(); val start=now-7*24*3600_000L; val src=SourceCatalog.all.filter{TextPolicy.phraseMatches(it.name+" "+it.aliases.joinToString(" "),d.vehicle)}.ifEmpty{SourceCatalog.all}; var result=searchPeriodProgressive(start,now,src,false){}; val filtered=result.items.filter{TextPolicy.phraseMatches(it.title+" "+it.snippet,d.subject)}.map{it.copy(demand=true,matchedDemand="${d.vehicle} + ${d.subject}")}; val before=db.links(); db.insertNews(filtered); val nc=filtered.count{UrlPolicy.canonicalizeUrl(it.link)!in before}; db.updateDemandStatus(d.id,filtered.size,nc,null); return DemandSearchResult(d,filtered,nc,result.errors,result.errorDetails.firstOrNull()) }
}

class VideoRepository(private val db:VideoDb){
 suspend fun search(start:Long,end:Long,sources:List<VideoSource>,terms:List<String>,demands:List<Demand>,onUpdate:(VideoSearchUpdate)->Unit):VideoSearchResult=withContext(Dispatchers.IO){
   val out=linkedMapOf<String,VideoItem>(); val issues=mutableListOf<VideoSourceIssue>(); var processed=0
   for(src in sources){ try{ val doc=Jsoup.connect(src.url).userAgent("Mozilla/5.0").timeout(12000).get(); val links=doc.select("a[href]").take(250); for(a in links){ val href=a.absUrl("href"); val title=a.text().trim(); if(href.isBlank()||title.length<8) continue; val lower=href.lowercase(); val specific=when(src.type){VideoSourceType.YOUTUBE -> "watch?v=" in lower || "/shorts/" in lower; VideoSourceType.GLOBOPLAY -> "/v/" in lower || "/video/" in lower; else -> "video" in lower || "/v/" in lower}; if(!specific) continue; val matched=terms.firstOrNull{TextPolicy.phraseMatches(title,it)}; val md=demands.firstOrNull{TextPolicy.phraseMatches(src.name+" "+src.aliases.joinToString(" "),it.vehicle)&&TextPolicy.phraseMatches(title,it.subject)}; val v=VideoItem(title=title,sourceId=src.id,sourceName=src.name,publishedAt=null,link=href,matchedTerm=matched,matchedDemand=md?.let{"${it.vehicle} + ${it.subject}"}); out[UrlPolicy.canonicalizeUrl(href)]=v } }catch(e:Exception){issues+=VideoSourceIssue(src.id,src.name,e.message?:"erro",1)};processed++;onUpdate(VideoSearchUpdate(LiveSearchProgress(true,currentSource=src.name,processed=processed,total=sources.size,found=out.size,errors=issues.size),out.values.toList(),issues)) }
   val before=db.links(); val items=out.values.toList(); db.insert(items); val nc=items.count{UrlPolicy.canonicalizeUrl(it.link)!in before}; onUpdate(VideoSearchUpdate(LiveSearchProgress(false,processed=processed,total=sources.size,found=items.size,newItems=nc,errors=issues.size),items,issues));VideoSearchResult(items,nc,issues.size,issues,processed)
 }
}
