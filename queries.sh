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
  'http://localhost:8080/_ah/api/tinyinsta/v1/user/register' \
  --header 'Accept: application/json' \
  --header 'Content-Type: application/json' \
  --data '{"name":"Vicky","username":"mhoangvslev"}' \
  --compressed

