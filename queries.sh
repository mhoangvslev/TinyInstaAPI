curl --request POST \
  'http://localhost:8080/_ah/api/tinyinsta/v1/post/create' \
  --header 'Accept: application/json' \
  --header 'Content-Type: application/json' \
  --data '{"imageUrl":"test","caption":"caption","postedBy":1}' \
  --compressed

curl --request GET \
  'http://localhost:8080/_ah/api/tinyinsta/v1/user' \
  --header 'Accept: application/json' \
  --header 'Content-Type: application/json' \
  --data '{"username": "hoang"}' \
  --compressed

curl --request POST \
  'http://localhost:8080/_ah/api/tinyinsta/v1/user/register/mhoangvslev/Hoang/avatar1' \
  --header 'Accept: application/json' \
  --header 'Content-Type: application/json' \
  --compressed

curl --request PUT \
  'http://localhost:8080/_ah/api/tinyinsta/v1/user/1/follow/2' \
  --header 'Accept: application/json' \
  --compressed

curl --request GET \
  'http://localhost:8080/_ah/api/tinyinsta/v1/user/2/followers' \
  --header 'Accept: application/json' \
  --compressed

