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
  'http://localhost:8080/_ah/api/tinyinsta/v1/user/all' \
  --header 'Accept: application/json' \
  --header 'Content-Type: application/json' \
  --compressed

curl --request GET \
  'http://localhost:8080/_ah/api/tinyinsta/v1/counter/all' \
  --header 'Accept: application/json' \
  --header 'Content-Type: application/json' \
  --compressed

curl --request POST \
  'http://localhost:8080/_ah/api/tinyinsta/v1/user/register/mhoangslev/Hoang/avatar1' \
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
