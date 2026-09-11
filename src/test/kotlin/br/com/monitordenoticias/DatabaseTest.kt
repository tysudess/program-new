package br.com.monitordenoticias
import br.com.monitordenoticias.android.*
import kotlin.test.*
import java.nio.file.Files
class DatabaseTest{
 @Test fun newsDedup(){val d=Files.createTempDirectory("mn").toFile();NewsDb(d).use{db->val n=News(title="Teste",source="Fonte",date=1,link="https://x.com/a?utm_source=z");assertEquals(1,db.insertNews(listOf(n)));assertEquals(0,db.insertNews(listOf(n.copy(link="https://www.x.com/a"))));assertEquals(1,db.listNews().size)}}
 @Test fun termsAndDemands(){val d=Files.createTempDirectory("mn2").toFile();NewsDb(d).use{db->db.addTerm("  Polícia   Federal ");db.addTerm("Polícia Federal");assertEquals(1,db.listTerms().size);db.addDemand("G1","GDF");db.addDemand("G1","GDF");assertEquals(1,db.listDemands().size)}}
}
