# Lambda-architecture for YouTube data

Counts top tags for https://www.kaggle.com/datasnaek/youtube-new

dataset https://www.kaggle.com/datasnaek/youtube-new?select=RUvideos.csv

### How to run
- `sbt assembly`
- `docker-compose build`
- `docker-compose up -d`

### Result
- the result can be viewed in Postgres tables by url 
`jdbc:postgresql://postgres:5432/youtube`
