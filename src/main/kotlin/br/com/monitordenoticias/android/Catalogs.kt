package br.com.monitordenoticias.android

object SourceCatalog {
    const val ALL_REGION="Todas"; const val NATIONAL_REGION="Nacional"
    val national=listOf(
        MediaSource("g1","G1","https://g1.globo.com/",NATIONAL_REGION,aliases=listOf("Globo")),
        MediaSource("cnn-brasil","CNN Brasil","https://www.cnnbrasil.com.br/",NATIONAL_REGION),
        MediaSource("folha","Folha de S.Paulo","https://www.folha.uol.com.br/",NATIONAL_REGION),
        MediaSource("oglobo","O Globo","https://oglobo.globo.com/",NATIONAL_REGION),
        MediaSource("estadao","Estadão","https://www.estadao.com.br/",NATIONAL_REGION),
        MediaSource("jovempan","Jovem Pan","https://jovempan.com.br/",NATIONAL_REGION),
        MediaSource("correio-braziliense","Correio Braziliense","https://www.correiobraziliense.com.br/","Centro-Oeste","DF"),
        MediaSource("jornal-brasilia","Jornal de Brasília","https://jornaldebrasilia.com.br/","Centro-Oeste","DF"),
        MediaSource("estado-minas","Estado de Minas","https://www.em.com.br/","Sudeste","MG")
    )
    val all=national; val byId=all.associateBy{it.id}; fun selected(ids:Set<String>)=if(ids.isEmpty()) all else ids.mapNotNull(byId::get)
}
object VideoSourceCatalog {
    val all=listOf(
        VideoSource("youtube-cnn","CNN Brasil - YouTube","https://www.youtube.com/@CNNbrasil",VideoSourceType.YOUTUBE,"Nacional"),
        VideoSource("youtube-jovempan","Jovem Pan News - YouTube","https://www.youtube.com/@jovempannews",VideoSourceType.YOUTUBE,"Nacional"),
        VideoSource("globoplay-jn","Jornal Nacional - Globoplay","https://globoplay.globo.com/jornal-nacional/",VideoSourceType.GLOBOPLAY,"Nacional"),
        VideoSource("globoplay-df1","DF1 - Globoplay","https://globoplay.globo.com/df1/",VideoSourceType.GLOBOPLAY,"DF"),
        VideoSource("g1-videos","G1 Vídeos","https://g1.globo.com/videos/",VideoSourceType.PORTAL,"Nacional")
    ); val byId=all.associateBy{it.id}; val defaultIds=all.map{it.id}.toSet(); fun selected(ids:Set<String>)=if(ids.isEmpty()) all else ids.mapNotNull(byId::get)
}
