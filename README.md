# Monitor de Notícias v4.0.2 — Clean-room

Reimplementação independente, do zero, de um monitor desktop de notícias e vídeos para Windows, baseada em um contrato funcional. Não contém código binário/decompilado do aplicativo original.

## O que já está implementado

- Interface Jetpack Compose Desktop com 8 áreas: Início, Notícias, Vídeos, Termos, Demandas, Fontes, Histórico e Configurações.
- SQLite local (`data/news.db` e `data/videos.db`).
- Termos de notícias e termos de vídeos independentes.
- Demandas `Veículo + Assunto` com execução individual ou em lote.
- Busca real de notícias via Google News RSS, filtrada por termos/fontes/período.
- Coleta modular de candidatos de vídeo em fontes cadastradas.
- Deduplicação por URL canônica e indicação de itens novos.
- Histórico persistente e exportação CSV UTF-8.
- Automação por intervalo para notícias/demandas e por horários para vídeos.
- Proxy JVM, senha protegida por DPAPI no Windows e teste de conexão.
- Inicialização por usuário via `HKCU\\Software\\Microsoft\\Windows\\CurrentVersion\\Run`.
- Testes unitários para normalização, matching, URL e SQLite.

## Requisitos para desenvolvimento

- JDK 17 ou superior.
- Windows 10/11 x64 para gerar o `.exe` nativo.

O aplicativo empacotado pelo Compose/jpackage inclui runtime próprio; o usuário final não precisa instalar Java.

## Executar em desenvolvimento

```bat
gradlew.bat run
```

Linux/macOS:

```bash
./gradlew run
```

## Testes

```bat
gradlew.bat clean test
```

## Gerar pacote Windows

Execute **no Windows x64**:

```bat
scripts\\build-windows.bat
```

ou:

```bat
gradlew.bat clean test packageDistributionForCurrentOS
```

Os artefatos ficam em `build/compose/binaries/`.

## Dados portáteis

Ao executar a partir da pasta do aplicativo, os dados são gravados em:

- `data/news.db`
- `data/videos.db`
- `data/preferences/`
- `data/logs/`

## Observações

Sites de notícias e vídeo alteram HTML, bloqueios e políticas com frequência. Os coletores foram isolados para que fontes possam ser ajustadas sem reescrever o aplicativo inteiro. O projeto evita resultados hardcoded; as pesquisas dependem das respostas reais das fontes.

## GitHub Actions

O repositório inclui workflows em `.github/workflows/`:

- `ci.yml`: executa `clean test` em Ubuntu a cada push/pull request.
- `build-windows.yml`: compila e publica os binários Windows como artefato do GitHub Actions.
- `release-windows.yml`: ao criar uma tag `v*` (por exemplo `v4.0.2`), compila no Windows, cria `MonitorDeNoticias-Windows.zip` e publica uma GitHub Release. Também pode ser iniciado manualmente pela aba **Actions**.

Para gerar uma release automática:

```bash
git tag v4.0.2
git push origin v4.0.2
```

Depois acompanhe a execução em **GitHub > Actions > Release Windows**. O ZIP também ficará disponível na página **Releases** do repositório.
