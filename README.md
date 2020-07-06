# sample proxy

This api exposes a rest api to send a Book entity.

Instructions

Run the docker file

See the description of the UI using the exposed swagger interface
http://localhost:8080/api/swagger

Send a sample Book using the post api
http://localhost:8080/api/book

Sample Request Message:
{
  "isbn": "9781593275846",
  "title": "Eloquent JavaScript Second Edition",
  "subtitle": "A Modern Introduction to Programming",
  "author": "Marijn Haverbeke"
}

Sample Response Message:
ResponseApi{code=200, message='Success'}

Sample Failed validation Response Message:
ResponseApi{code=422, message='Success'}

Sample Response Message:
ResponseApi{code=500, message='Success'}