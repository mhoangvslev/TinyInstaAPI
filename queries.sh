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
  'https://tinyinstagram.appspot.com/_ah/api/tinyinsta/v1/user/register/zwalsh/Zoe/AMIfv95F-3cz2TeKBBjMMvo3HIINt30JocXTw8FfxpawA6wuGqcnT-Seb523vdDpvWI6HBjbchpQVMeiDn_HTGtc630K0Oz7HFYbtEIjCMf_maax1gcGMURxlXn1ORn_1cgFFalRhKYKr--7lcCNdDIEd1rzfpg57FJGIo-f73FbJecGCLDXs9LFQK-HG8fmg79J1-WWy6ZZy1lT25RI8cbbFYXfNwoIB2MGBr-zBul8NJTzEodaU3p2HM8Qup3FMp-2xG6z-OXEtdtMaRNt8cohunO2spHu1w' \
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
