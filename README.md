# TinyInstaAPI

## Fonctionalités
- Endpoints: ```TinyInstaEndpoint.java```
- ShardedCounter: ```Counter.java, CounterShard.java, PostRepository.java, ShardedCounter.java```
- Blobstore: ```ImageServlet.java, PostServlet.java, UserServlet.java```
- Benchmark: API Methods pour test en ligne
- WebApp (incomplet): [projet ```Mithril.js```](https://github.com/mhoangvslev/TinyInstagram), [déploiement](tinyinstagram.appspot.com).

## Autres
- ```launch.sh```: déployer sur localhost. google-cloud-datastore-emulator doit etre installé 
- ```deploy.sh```: déployer sur Google Cloud Platform
- ```queries.sh```: commandes curl pour tester le endpoint
- Endpoints Portal (sur demande): https://endpointsportal.tinyinstagram.cloud.goog/

## Benchmark
### **Test 1:** How much time it takes to post of message if followed by 10, 100 and 500 followers? (average on 30 measures)
```
https://tinyinstagram.appspot.com/_ah/api/tinyinsta/v1/benchmark/test1/{nbFollowers} (GET)
```

Un utilisateur crée un post, ```nbFollowers``` followers mettent à jour son fil d'actualité.

| 10    	| 100    	| 500     	|
|-------	|--------	|---------	|
| 68 ms 	| 318 ms 	| 1448 ms 	|

### **Test 2:** How much time it takes to retrieve the last 10, 100 and 500 last messages ? (average of 30 measures)

Un utilisateur crée ```nbPosts``` posts. Test: un follower met à jour son fil d'actualité.

```
https://tinyinstagram.appspot.com/_ah/api/tinyinsta/v1/benchmark/test2/{nbPost} (GET)
```

| 10   	| 100  	| 500  	|
|------	|------	|------	|
| 5 ms 	| 5 ms 	| 7 ms 	|

### **Test 3:** How much “likes” can you do per second ?? (average on 30 measures)

```100 users like 1 post in 1.913ms, thus 0.0005 likes/post/sec```
