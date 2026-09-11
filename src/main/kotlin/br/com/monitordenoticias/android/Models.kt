package br.com.monitordenoticias.android

data class News(val id:Long=0,val title:String,val source:String,val date:Long?,val link:String,val snippet:String="",val important:Boolean=false,val demand:Boolean=false,val matchedTerm:String?=null,val matchedDemand:String?=null,val capturedAt:Long=System.currentTimeMillis())
data class VideoItem(val id:Long=0,val title:String,val sourceId:String,val sourceName:String,val publishedAt:Long?,val link:String,val summary:String="",val matchedTerm:String?=null,val matchedDemand:String?=null,val capturedAt:Long=System.currentTimeMillis()){ val relevant get()=matchedTerm!=null; val demand get()=matchedDemand!=null }
data class Demand(val id:Long=0,val vehicle:String,val subject:String,val active:Boolean=true,val lastCheckedAt:Long?=null,val lastFoundCount:Int=0,val lastNewCount:Int=0,val lastError:String?=null)
data class MediaSource(val id:String,val name:String,val url:String,val region:String,val state:String?=null,val aliases:List<String> = emptyList(),val extraUrls:List<String> = emptyList())
enum class VideoSourceType { YOUTUBE, PORTAL, PROGRAM, GLOBOPLAY, REGIONAL_SWEEP }
data class VideoSource(val id:String,val name:String,val url:String,val type:VideoSourceType,val region:String?=null,val aliases:List<String> = emptyList(),val searchUrls:List<String> = emptyList())
data class LiveSearchProgress(val running:Boolean=false,val startedAt:Long=0,val currentSource:String?=null,val currentQuery:String?=null,val processed:Int=0,val total:Int=0,val found:Int=0,val newItems:Int=0,val errors:Int=0)
data class SearchResult(val items:List<News>,val newItems:Int,val errors:Int,val errorDetails:List<String> = emptyList(),val processed:Int=0)
data class VideoSearchResult(val items:List<VideoItem>,val newItems:Int,val errors:Int,val issues:List<VideoSourceIssue> = emptyList(),val processed:Int=0)
data class DemandSearchResult(val demand:Demand,val items:List<News>,val newItems:Int,val errors:Int,val error:String?=null)
data class DemandSweepResult(val checked:Int,val found:Int,val newItems:Int,val errors:Int)
data class NewsSearchUpdate(val progress:LiveSearchProgress,val items:List<News> = emptyList())
data class VideoSearchUpdate(val progress:LiveSearchProgress,val items:List<VideoItem> = emptyList(),val issues:List<VideoSourceIssue> = emptyList())
data class VideoSourceIssue(val sourceId:String,val sourceName:String,val error:String,val failures:Int)
data class AutoReport(val lastAttempt:Long?=null,val lastCompleted:Long?=null,val found:Int=0,val newItems:Int=0,val errors:Int=0,val error:String?=null,val checked:Int?=null,val relevant:Int?=null)
data class PeriodPreset(val label:String,val start:Long,val end:Long)
enum class VideoFilter { ALL, RELEVANT, DEMANDS }
