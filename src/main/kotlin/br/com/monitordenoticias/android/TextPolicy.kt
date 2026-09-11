package br.com.monitordenoticias.android

import java.net.URI
import java.text.Normalizer

object TextPolicy {
    val STOP_WORDS=setOf("a","o","as","os","de","da","do","das","dos","e","em","no","na","nos","nas","para","por","com","um","uma")
    fun normalize(v:String):String = Normalizer.normalize(v,Normalizer.Form.NFD).replace("\\p{M}+".toRegex(),"").lowercase().replace("<[^>]+>".toRegex()," ").replace("[^a-z0-9]+".toRegex()," ").trim().replace("\\s+".toRegex()," ")
    fun inflectionVariants(token:String):Set<String>{ val n=normalize(token); if(n.length<4) return setOf(n); val s=mutableSetOf(n); when { n.endsWith("oes") -> s+=n.dropLast(3)+"ao"; n.endsWith("ais") -> s+=n.dropLast(3)+"al"; n.endsWith("s") -> s+=n.dropLast(1); else -> s+=n+"s" }; if(n.endsWith("a")) s+=n.dropLast(1)+"o"; if(n.endsWith("o")) s+=n.dropLast(1)+"a"; return s }
    fun phraseMatches(text:String,phrase:String):Boolean { val t=normalize(text); val p=normalize(phrase); if(p.isBlank()) return false; if(" $t ".contains(" $p ")) return true; val tt=t.split(" ").filter{it.isNotBlank()}; val pt=p.split(" ").filter{it.isNotBlank() && it !in STOP_WORDS}; if(pt.isEmpty()) return false; return pt.all { q -> tt.any { x -> inflectionVariants(q).intersect(inflectionVariants(x)).isNotEmpty() } } }
}

object UrlPolicy {
    private val tracking=setOf("fbclid","gclid")
    fun canonicalizeUrl(url:String):String = try { val u=URI(url.trim()); val scheme=(u.scheme?:"https").lowercase(); var host=(u.host?:"").lowercase().removePrefix("www."); var path=(u.path?:"/").replace("/{2,}".toRegex(),"/"); if(path.length>1) path=path.trimEnd('/'); val q=(u.rawQuery?:"").split('&').filter{it.isNotBlank()}.filterNot{p-> val k=p.substringBefore('=').lowercase(); k.startsWith("utm_")||k in tracking}.sorted().joinToString("&"); "$scheme://$host$path" + if(q.isBlank()) "" else "?$q" } catch(_:Exception){url.trim().substringBefore('#')}
    fun samePage(a:String,b:String)=canonicalizeUrl(a)==canonicalizeUrl(b)
}
