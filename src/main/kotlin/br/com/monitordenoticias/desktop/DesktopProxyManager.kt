package br.com.monitordenoticias.desktop

import com.sun.jna.platform.win32.Crypt32Util
import java.io.File
import java.net.Authenticator
import java.net.PasswordAuthentication
import java.net.URL
import java.util.Base64
import java.util.Properties

object DesktopProxyManager {
 data class Settings(val enabled:Boolean=false,val host:String="",val port:Int=0,val user:String="",val domain:String="",val hasPassword:Boolean=false)
 data class SaveResult(val success:Boolean,val message:String)
 data class TestResult(val success:Boolean,val status:Int?,val message:String,val elapsedMs:Long)
 private fun file(base:File)=File(base,"data/preferences/proxy.properties")
 fun load(base:File):Settings{val p=Properties();val f=file(base);if(f.exists())f.inputStream().use(p::load);return Settings(p.getProperty("enabled","false").toBoolean(),p.getProperty("host","") ,p.getProperty("port","0").toIntOrNull()?:0,p.getProperty("user","") ,p.getProperty("domain","") ,!p.getProperty("passwordDpapi","").isNullOrBlank())}
 fun save(base:File,enabled:Boolean,host:String,port:Int,user:String,domain:String,password:String?):SaveResult=runCatching{val p=Properties();p["enabled"]=enabled.toString();p["host"]=host;p["port"]=port.toString();p["user"]=user;p["domain"]=domain;if(!password.isNullOrBlank())p["passwordDpapi"]=protectWithDpapi(password);val f=file(base);f.parentFile.mkdirs();f.outputStream().use{p.store(it,"proxy")};SaveResult(true,"Configurações salvas")}.getOrElse{SaveResult(false,it.message?:"Erro")}
 fun forgetPassword(base:File){val f=file(base);if(!f.exists())return;val p=Properties();f.inputStream().use(p::load);p.remove("passwordDpapi");f.outputStream().use{p.store(it,"proxy")}}
 fun isReady(base:File):Boolean{val s=load(base);return !s.enabled || (s.host.isNotBlank()&&s.port in 1..65535)}
 fun apply(base:File){val s=load(base);if(!s.enabled){clearJvmProxy();return};System.setProperty("http.proxyHost",s.host);System.setProperty("http.proxyPort",s.port.toString());System.setProperty("https.proxyHost",s.host);System.setProperty("https.proxyPort",s.port.toString());val p=Properties();val f=file(base);if(f.exists())f.inputStream().use(p::load);val pass=p.getProperty("passwordDpapi")?.takeIf{it.isNotBlank()}?.let(::unprotectWithDpapi);if(s.user.isNotBlank()&&pass!=null){Authenticator.setDefault(object:Authenticator(){override fun getPasswordAuthentication()=PasswordAuthentication(s.user,pass.toCharArray())})}}
 fun test(base:File):TestResult{val t=System.currentTimeMillis();return runCatching{apply(base);val c=URL("https://www.google.com/generate_204").openConnection() as java.net.HttpURLConnection;c.connectTimeout=7000;c.readTimeout=7000;val code=c.responseCode;TestResult(code in 200..399,code,"HTTP $code",System.currentTimeMillis()-t)}.getOrElse{TestResult(false,null,it.message?:"Erro",System.currentTimeMillis()-t)}}
 fun clearJvmProxy(){listOf("http.proxyHost","http.proxyPort","https.proxyHost","https.proxyPort").forEach(System::clearProperty);Authenticator.setDefault(null)}
 fun protectWithDpapi(value:String):String = if(System.getProperty("os.name").lowercase().contains("win")) Base64.getEncoder().encodeToString(Crypt32Util.cryptProtectData(value.toByteArray(Charsets.UTF_8))) else Base64.getEncoder().encodeToString(value.toByteArray(Charsets.UTF_8))
 fun unprotectWithDpapi(value:String):String {val b=Base64.getDecoder().decode(value);return if(System.getProperty("os.name").lowercase().contains("win")) String(Crypt32Util.cryptUnprotectData(b),Charsets.UTF_8) else String(b,Charsets.UTF_8)}
}
