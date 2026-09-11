package br.com.monitordenoticias
import br.com.monitordenoticias.android.*
import kotlin.test.*
class PolicyTest{
 @Test fun normalize(){assertEquals("policia federal",TextPolicy.normalize("Polícia  Federal!"))}
 @Test fun phrase(){assertTrue(TextPolicy.phraseMatches("Operações da Polícia Federal","policia federal"));assertFalse(TextPolicy.phraseMatches("Policial federalizado","policia federal"))}
 @Test fun inflection(){assertTrue("governo" in TextPolicy.inflectionVariants("governos"))}
 @Test fun canonical(){assertEquals("https://example.com/a",UrlPolicy.canonicalizeUrl("https://www.example.com/a/?utm_source=x#x"))}
 @Test fun same(){assertTrue(UrlPolicy.samePage("https://x.com/a?utm_source=z","https://www.x.com/a"))}
}
