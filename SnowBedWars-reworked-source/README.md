# Snow BedWars

Plugin BedWars autoral para **Spigot 1.8.8/1.8.9**, compilável em Java 8 ou 11.

## Estrutura

```
src/main/java/com/roberto/snow/
  Snow.java                 bootstrap e integração de serviços
  arena/                    ciclo de vida, times e persistência
  command/                  /bw, /leave, /rejoin e /shout
  generator/                geradores de recursos e hologramas ArmorStand
  listener/                 cama, entradas, morte e GUI
  scoreboard/               sidebar sem reset/flicker
  shop/                     loja e upgrades nativos
  util/                     mensagens e localização
src/main/resources/         plugin.yml, config.yml, language.yml
```

## Compilar e instalar

Execute `mvn clean package` com Java 8 ou 11 e coloque `target/Snow.jar` em `plugins/`.

## Comandos

`/bw join <arena>`, `/bw leave`, `/bw list`, `/bw gui`, `/bw start`, `/bw stats`, `/bw upgrades`, `/bw teleporter`, `/bw lang`, `/bw setup <arena>`, `/bw create <arena> <solo|doubles|3v3v3v3|4v4v4v4>`, `/bw setworld <arena> <mundo>`, `/bw setspawn <arena> <team>`, `/bw setbed <arena> <team>`, `/bw generator <arena> <iron|gold|diamond|emerald>`, `/bw reload`.

Aliases de manutenção do projeto de referência são reconhecidos e respondem com instruções de migração: `build`, `clonearena`, `delarena`, `enable`, `disable`, `arenalist`, `arenagroup`, `setlobby`, `npc`, `level`, `quests`, `prestige`, `party` e os subcomandos de setup.
