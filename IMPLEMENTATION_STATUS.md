# Status da implementação

Este repositório é uma implementação clean-room funcional inicial do contrato fornecido.

## Implementado
- Projeto Kotlin/Compose Desktop e configuração de empacotamento nativo.
- 8 áreas principais da interface.
- Persistência SQLite de notícias, vídeos, termos e demandas.
- Busca de notícias por RSS do Google News com termos, fontes e período.
- Coleta modular de candidatos de vídeo por páginas das fontes.
- Matching textual com normalização/flexões simples.
- URL canônica, deduplicação e identificação de itens novos.
- Histórico e exportação CSV.
- Automação de notícias/demandas por intervalo e vídeos por horários.
- Proxy JVM com DPAPI no Windows.
- Startup via HKCU no Windows.
- Testes do núcleo e banco.

## Evoluções recomendadas para paridade integral
- Aumentar o catálogo nacional/estadual para todas as UFs.
- Implementar coletores especializados Globoplay Trechos/Edições/Jarvis em arquivos independentes.
- Resolver feeds/canais oficiais do YouTube por channel ID/API pública ou RSS oficial.
- Enriquecer datas/metadados de vídeos individualmente.
- Adicionar filtros avançados e telas completas de proxy/automação.
- Ampliar os testes de automação e integração HTTP.

A geração do EXE deve ser feita em Windows 10/11 x64 usando `scripts/build-windows.bat`.
