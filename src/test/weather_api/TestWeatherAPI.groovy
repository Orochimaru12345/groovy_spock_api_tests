package test.weather_api

import groovy.json.JsonSlurper
import groovy.transform.SourceURI
import spock.lang.Specification

// About request:
// https://openweathermap.org/current#rectangle
// About response:
// https://openweathermap.org/current#current_JSON
class TestWeatherAPI extends Specification {
    // configuration
    static def apiKey = 'undefined'
    static def minT = -100
    static def maxT = -10
    static def debug = false
    // city center coordinates
    static def lat = 59.8944
    static def lon = 30.2642
    static def halfSide = 0.045 // ~5km
    // request bBox parameter
    static def bBox = ''
    // response
    static def weather
    static def config

    def 'setupSpec'() {
        config = readConfig()
        println("config: " + config)

        apiKey = config.apiKey as String
        println('Key: ' + apiKey)
        minT = config.minT as float
        println('Min: ' + minT)
        maxT = config.maxT as float
        println('Max: ' + maxT)

        debug = config.debug as boolean
        println('Debug: ' + debug)

        bBox = createBBox()
        println('Box: ' + bBox)

        if (debug) {
            weather = setDebugWeather()
        } else {
            weather = getRealWeather()
        }
    }

    static def createBBox() {
        // lon-left,lat-bottom,lon-right,lat-top,zoom
        def lonLeft = lon - halfSide
        def latBottom = lat - halfSide
        def lonRight = lon + halfSide
        def latTop = lat + halfSide
        return '' + lonLeft + ',' + latBottom + ',' + lonRight + ',' + latTop + ',1'
    }

    static def readConfig() {
        @SourceURI
        URI sourceURI

        def jsonSlurper = new JsonSlurper()
        def config = jsonSlurper.parse(
                new File(sourceURI.getPath().replace('TestWeatherAPI.groovy', 'config.json'))
        )

        return config
    }

    @SuppressWarnings('unused')
    static def getRealWeather() {
        def url = 'http://api.openweathermap.org/data/2.5/box/city?bbox=' + bBox + '&appid=' + apiKey + '&units=metric'
        def responseText = url.toURL().text
        println('Weather in Saint Petersburg:\n' + responseText)

        return new JsonSlurper().parseText(responseText)
    }

    @SuppressWarnings('GroovyAssignabilityCheck')
    def 'test: check that temperature at Saint Petersburg is valid (by rectangular location)'() {
        expect:
        // response must be JSON object
        weather instanceof Map
        // key 'list' in response must exist
        weather.containsKey('list')
        // 'list' must be JSON array
        weather['list'] instanceof ArrayList
        // 'list' must have at least 1 item
        weather['list'].size() > 0
        // item must be JSON object
        weather['list'][0] instanceof Map
        // item must have 'name' key
        weather['list'][0].containsKey('name')
        // 'name' must be String
        weather['list'][0]['name'] instanceof String
        // 'name' must be 'Saint Petersburg'
        weather['list'][0]['name'] == 'Saint Petersburg'
        // item must have 'main' key
        weather['list'][0].containsKey('main')
        // 'main' must be JSON object
        weather['list'][0]['main'] instanceof Map
        // 'main' must have key 'temp'
        weather['list'][0]['main'].containsKey('temp')
        // 'temp' must be number
        weather['list'][0]['main']['temp'] instanceof Number
        // 'temp' must be between minT & maxT
        weather['list'][0]['main']['temp'] >= minT
        weather['list'][0]['main']['temp'] <= maxT
    }

    @SuppressWarnings('unused')
    private static def setDebugWeather() {
        weather = [
                "cod"     : 200,
                "calctime": 0.000423342,
                "cnt"     : 1,
                "list"    : [
                        [
                                "id"        : 498817,
                                "dt"        : 1620511745,
                                "name"      : "Saint Petersburg",
                                "coord"     : [
                                        "Lon": 30.2642,
                                        "Lat": 59.8944
                                ],
                                "main"      : [
                                        "temp"      : -22.65,
                                        "feels_like": -0.32,
                                        "temp_min"  : 2.22,
                                        "temp_max"  : 3,
                                        "pressure"  : 1009,
                                        "humidity"  : 81
                                ],
                                "visibility": 10000,
                                "wind"      : [
                                        "speed": 3,
                                        "deg"  : 260
                                ],
                                "rain"      : null,
                                "snow"      : null,
                                "clouds"    : [
                                        "today": 0
                                ],
                                "weather"   : [
                                        [
                                                "id"         : 800,
                                                "main"       : "Clear",
                                                "description": "clear sky",
                                                "icon"       : "01n"
                                        ]
                                ]
                        ]
                ]
        ]
        return weather
    }
}

