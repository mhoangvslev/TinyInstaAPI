# TinyInstaAPI

## Fonctionalités
- Endpoints: ```TinyInstaEndpoint.java```
- ShardedCounter: ```Counter.java, CounterShard.java, PostRepository.java, ShardedCounter.java```
- Blobstore: ```ImageServlet.java, PostServlet.java, UserServlet.java```
- Benchmark: ```test/java/Benchmark.java```
- WebApp (incomplet): [projet ```Mithril.js```](https://github.com/mhoangvslev/TinyInstagram), [voir déploiement](tinyinstagram.appspot.com).

## Autres
- ```launch.sh```: déployer sur localhost. google-cloud-datastore-emulator doit etre installé 
- ```deploy.sh```: déployer sur Google Cloud Platform
- ```queries.sh```: commandes curl pour tester le endpoint