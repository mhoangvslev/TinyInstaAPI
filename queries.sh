curl --request POST \
  'http://localhost:8080/_ah/api/tinyinsta/v1/post/create' \
  --header 'Accept: application/json' \
  --header 'Content-Type: application/json' \
  --data '{"imageURL":"test","caption":"caption","ownerId":1}' \
  --compressed

curl --request GET \
  'http://localhost:8080/_ah/api/tinyinsta/v1/user' \
  --header 'Accept: application/json' \
  --header 'Content-Type: application/json' \
  --data '{"username": "hoang"}' \
  --compressed

curl --request GET \
  'http://localhost:8080/_ah/api/tinyinsta/v1/post/all' \
  --header 'Accept: application/json' \
  --header 'Content-Type: application/json' \
  --compressed

curl --request GET \
  'http://localhost:8080/_ah/api/tinyinsta/v1/user/find' \
  --header 'Accept: application/json' \
  --header 'Content-Type: application/json' \
  --compressed

curl --request GET \
  'http://localhost:8080/_ah/api/tinyinsta/v1/counter/all' \
  --header 'Accept: application/json' \
  --header 'Content-Type: application/json' \
  --compressed

curl --request POST \
  'https://tinyinstagram.appspot.com/_ah/api/tinyinsta/v1/user/register/mhoangvslev/Hoang/AMIfv96AVIvz8KpBt3bdy5ycJWCB_bpAacffdC7oa71KNKX7UmsNPaPlWFfI3uZrJcEisHFFg4cOhQhI97Sl4RRFeoL5YLtedXBViCS6Qzwt3IFMqZkiEH5rwyDNrZHhZAkg9iI4EVBxLepSAaqQSvOSME4bmDiFs6xs29yN9zqZP0ziCnevjqaAamcs9Gs2i1JM0IKV8NBrdXRpBp6GOaV_p72-resN6kLZJnuUP69FhoQ0k7jkAsSixmio4edolx5qeU3RgrF5sInulEKygmpaQjwwDdSPVoOAw18dK4DRcgBsR0TE8xUe1wmAe1LhVNl1sP24q8rM' \
  --header 'Accept: application/json' \
  --header 'Content-Type: application/json' \
  --compressed

curl --request PUT \
  'http://localhost:8080/_ah/api/tinyinsta/v1/user/3/like/2' \
  --header 'Accept: application/json' \
  --compressed

curl --request PUT \
  'http://localhost:8080/_ah/api/tinyinsta/v1/user/1/unlike/2' \
  --header 'Accept: application/json' \
  --compressed

curl --request PUT \
  'http://localhost:8080/_ah/api/tinyinsta/v1/user/1/follow/2' \
  --header 'Accept: application/json' \
  --compressed

curl --request GET \
  'http://localhost:8080/_ah/api/tinyinsta/v1/user/2/followers' \
  --header 'Accept: application/json' \
  --compressed

curl --request DELETE \
  'http://localhost:8080/_ah/api/tinyinsta/v1/user/delete/1' \
  --header 'Accept: application/json' \
  --compressed

curl --request DELETE \
  'http://localhost:8080/_ah/api/tinyinsta/v1/post/delete/1' \
  --header 'Accept: application/json' \
  --compressed

curl --request DELETE \
  'http://localhost:8080/_ah/api/tinyinsta/v1/user/delete/all' \
  --header 'Accept: application/json' \
  --compressed