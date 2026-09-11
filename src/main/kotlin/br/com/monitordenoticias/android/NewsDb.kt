package br.com.monitordenoticias.android

import java.io.File
import java.sql.Connection
import java.sql.DriverManager

class NewsDb(baseDir:File):AutoCloseable{
    private val c:Connection
    init { baseDir.mkdirs(); c=DriverManager.getConnection("jdbc:sqlite:${File(baseDir,"news.db").absolutePath}"); c.createStatement().use{it.execute("PRAGMA journal_mode=WAL"); it.execute("CREATE TABLE IF NOT EXISTS news(id INTEGER PRIMARY KEY AUTOINCREMENT,title TEXT NOT NULL,source TEXT NOT NULL,published_at INTEGER,link TEXT NOT NULL UNIQUE,snippet TEXT,important INTEGER NOT NULL DEFAULT 0,demand INTEGER NOT NULL DEFAULT 0,matched_term TEXT,matched_demand TEXT,captured_at INTEGER NOT NULL)"); it.execute("CREATE INDEX IF NOT EXISTS idx_news_pub ON news(published_at)"); it.execute("CREATE TABLE IF NOT EXISTS terms(id INTEGER PRIMARY KEY AUTOINCREMENT,term TEXT UNIQUE NOT NULL,created_at INTEGER)"); it.execute("CREATE TABLE IF NOT EXISTS demands(id INTEGER PRIMARY KEY AUTOINCREMENT,vehicle TEXT NOT NULL,subject TEXT NOT NULL,active INTEGER DEFAULT 1,created_at INTEGER,last_checked_at INTEGER,last_found_count INTEGER DEFAULT 0,last_new_count INTEGER DEFAULT 0,last_error TEXT, UNIQUE(vehicle,subject))") } }
    @Synchronized fun insertNews(items:List<News>):Int { var n=0; c.prepareStatement("INSERT OR IGNORE INTO news(title,source,published_at,link,snippet,important,demand,matched_term,matched_demand,captured_at) VALUES(?,?,?,?,?,?,?,?,?,?)").use{p-> for(x in items){p.setString(1,x.title);p.setString(2,x.source); if(x.date==null)p.setNull(3,java.sql.Types.BIGINT) else p.setLong(3,x.date);p.setString(4,UrlPolicy.canonicalizeUrl(x.link));p.setString(5,x.snippet);p.setInt(6,if(x.important)1 else 0);p.setInt(7,if(x.demand)1 else 0);p.setString(8,x.matchedTerm);p.setString(9,x.matchedDemand);p.setLong(10,x.capturedAt); n+=p.executeUpdate()} }; return n }
    fun listNews(limit:Int=1000):List<News>{ val out=mutableListOf<News>(); c.prepareStatement("SELECT * FROM news ORDER BY COALESCE(published_at,0) DESC,captured_at DESC LIMIT ?").use{p->p.setInt(1,limit);p.executeQuery().use{r->while(r.next())out+=News(r.getLong("id"),r.getString("title"),r.getString("source"),r.getLong("published_at").let{if(r.wasNull())null else it},r.getString("link"),r.getString("snippet")?:"",r.getInt("important")!=0,r.getInt("demand")!=0,r.getString("matched_term"),r.getString("matched_demand"),r.getLong("captured_at"))}};return out}
    fun links()=listNews(100000).map{UrlPolicy.canonicalizeUrl(it.link)}.toSet()
    fun listTerms():List<String>{val o=mutableListOf<String>();c.createStatement().executeQuery("SELECT term FROM terms ORDER BY term COLLATE NOCASE").use{while(it.next())o+=it.getString(1)};return o}
    fun addTerm(t:String){val v=t.trim().replace("\\s+".toRegex()," ");if(v.isNotBlank())c.prepareStatement("INSERT OR IGNORE INTO terms(term,created_at) VALUES(?,?)").use{it.setString(1,v);it.setLong(2,System.currentTimeMillis());it.executeUpdate()}}
    fun removeTerm(t:String){c.prepareStatement("DELETE FROM terms WHERE term=?").use{it.setString(1,t);it.executeUpdate()}}
    fun listDemands():List<Demand>{val o=mutableListOf<Demand>();c.createStatement().executeQuery("SELECT * FROM demands ORDER BY id DESC").use{r->while(r.next())o+=Demand(r.getLong("id"),r.getString("vehicle"),r.getString("subject"),r.getInt("active")!=0,r.getLong("last_checked_at").let{if(r.wasNull())null else it},r.getInt("last_found_count"),r.getInt("last_new_count"),r.getString("last_error"))};return o}
    fun addDemand(v:String,s:String){c.prepareStatement("INSERT OR IGNORE INTO demands(vehicle,subject,created_at) VALUES(?,?,?)").use{it.setString(1,v.trim());it.setString(2,s.trim());it.setLong(3,System.currentTimeMillis());it.executeUpdate()}}
    fun removeDemand(id:Long){c.prepareStatement("DELETE FROM demands WHERE id=?").use{it.setLong(1,id);it.executeUpdate()}}
    fun updateDemandStatus(id:Long,found:Int,newCount:Int,error:String?){c.prepareStatement("UPDATE demands SET last_checked_at=?,last_found_count=?,last_new_count=?,last_error=? WHERE id=?").use{it.setLong(1,System.currentTimeMillis());it.setInt(2,found);it.setInt(3,newCount);it.setString(4,error);it.setLong(5,id);it.executeUpdate()}}
    fun clearHistory(){c.createStatement().executeUpdate("DELETE FROM news")}
    override fun close(){c.close()}
}
