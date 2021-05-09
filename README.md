# groovy_spock_api_tests

### Weather API
[https://openweathermap.org/current](https://openweathermap.org/current)

### SetUp
```shell
.\src\test\weather_api\config.json
```
|name|description
|----|-----------
|apiKey|API key for `http://api.openweathermap.org/data/2.5/box/city`.
|minT| Minimum valid temperature. 
|maxT| Maximum valid temperature.
|debug| If `false` send real API request. If `true` use made up data.
