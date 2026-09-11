package br.com.monitordenoticias.android
import java.io.File
import java.util.Properties
class AppPreferences(private val file:File){private val p=Properties().apply{if(file.exists())file.inputStream().use(::load)};private fun save(){file.parentFile.mkdirs();file.outputStream().use{p.store(it,"MonitorDeNoticias")}};fun get(k:String,d:String="")=p.getProperty(k,d);fun put(k:String,v:String){p[k]=v;save()};fun getBool(k:String,d:Boolean=false)=get(k,d.toString()).toBoolean();fun putBool(k:String,v:Boolean)=put(k,v.toString());fun getInt(k:String,d:Int)=get(k,d.toString()).toIntOrNull()?:d}
class VideoTermStore(private val prefs:AppPreferences){fun list()=prefs.get("videoTerms").split("||").filter{it.isNotBlank()};fun save(v:List<String>)=prefs.put("videoTerms",v.distinct().joinToString("||"))}
object DesktopEstablishedTerms{val defaults=listOf("Polícia Federal","Governo Federal","Congresso Nacional")}
