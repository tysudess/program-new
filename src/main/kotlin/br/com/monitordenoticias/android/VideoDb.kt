package br.com.monitordenoticias.android
import java.io.File
import java.sql.DriverManager
class VideoDb(baseDir:File):AutoCloseable{
 private val c=DriverManager.getConnection("jdbc:sqlite:${File(baseDir.apply{mkdirs()},"videos.db").absolutePath}")
 init{c.createStatement().use{it.execute("CREATE TABLE IF NOT EXISTS videos(id INTEGER PRIMARY KEY AUTOINCREMENT,title TEXT,source_id TEXT,source_name TEXT,published_at INTEGER,link TEXT UNIQUE,summary TEXT,matched_term TEXT,matched_demand TEXT,captured_at INTEGER)");it.execute("CREATE INDEX IF NOT EXISTS idx_video_pub ON videos(published_at)")}}
 fun insert(items:List<VideoItem>):Int{var n=0;c.prepareStatement("INSERT OR IGNORE INTO videos(title,source_id,source_name,published_at,link,summary,matched_term,matched_demand,captured_at) VALUES(?,?,?,?,?,?,?,?,?)").use{p->items.forEach{x->p.setString(1,x.title);p.setString(2,x.sourceId);p.setString(3,x.sourceName);if(x.publishedAt==null)p.setNull(4,java.sql.Types.BIGINT) else p.setLong(4,x.publishedAt);p.setString(5,UrlPolicy.canonicalizeUrl(x.link));p.setString(6,x.summary);p.setString(7,x.matchedTerm);p.setString(8,x.matchedDemand);p.setLong(9,x.capturedAt);n+=p.executeUpdate()}};return n}
 fun listAll(limit:Int=1000):List<VideoItem>{val o=mutableListOf<VideoItem>();c.prepareStatement("SELECT * FROM videos ORDER BY COALESCE(published_at,0) DESC,captured_at DESC LIMIT ?").use{p->p.setInt(1,limit);p.executeQuery().use{r->while(r.next())o+=VideoItem(r.getLong("id"),r.getString("title")?:"",r.getString("source_id")?:"",r.getString("source_name")?:"",r.getLong("published_at").let{if(r.wasNull())null else it},r.getString("link")?:"",r.getString("summary")?:"",r.getString("matched_term"),r.getString("matched_demand"),r.getLong("captured_at"))}};return o}
 fun links()=listAll(100000).map{UrlPolicy.canonicalizeUrl(it.link)}.toSet(); fun clear(){c.createStatement().executeUpdate("DELETE FROM videos")}; override fun close(){c.close()}
}
