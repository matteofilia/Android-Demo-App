const express = require('express')
const app = express()
const http = require('http')
const url = require("url")
const qstring = require("querystring")

const AMAZON_LIGHTSAIL = "35.183.237.171:3001"
const CARLETON_OPENSTACK = "134.117.216.77:3001"
const LOCALHOST = "127.0.0.1:3001"

// Change which API server the client server uses here
const API_SERVER = AMAZON_LIGHTSAIL

const PORT = process.env.PORT || 3000
const ROOT_DIR = "/public"
const INDEX_FILE = "assignment4.html"

// Static file service
app.use(express.static(__dirname + ROOT_DIR))

function getCoolCars(manufacturer, onResult) {
  var apiReq
  if (manufacturer != "")apiReq = "http://" + API_SERVER + "/api?manufacturer="+manufacturer
  else apiReq = "http://" + API_SERVER + "/api"

  console.log("API request: " + apiReq)
  http.request(apiReq, function(res) {
    // console.log(`Status: ${res.statusCode}`)
    // console.log(`Headers: ${JSON.stringify(res.headers)}`)

    var rawData = ""

    res.on("data", function(chunk) {
      rawData += chunk
    })

    res.on("end", function() {
      console.log("API request finished")
      let cars = JSON.parse(rawData)
      
      // Resolve the link to the server where image links are being loaded from
      for (let car of cars) {
        car.FullPictureLink = "http://" + API_SERVER + "/" + car.PictureLink
      }
      
      onResult(cars)
    })
  }).end()
}

function redirect(req, res) {
  let path = "/?" + url.parse(req.url).query
  console.log("Redirecting to: " + path)
  
  res.redirect(path)
  res.end()
}

app.get("/assignment4", redirect)
app.get("/assignment4.html", redirect)
app.get("/recipes.html", redirect)
app.get("/recipes", redirect)
app.get("/recipes.html", redirect)
app.get("/recipe", redirect)
app.get("/recipe.html", redirect)
app.get("/index.html", redirect)
app.get("/cars", redirect)
app.get("/cars.html", redirect)
app.get("/car", redirect)
app.get("/car.html", redirect)

app.get("/", function(req, res) {
  let file = __dirname + ROOT_DIR + "/" + INDEX_FILE

  // Parse query, if any
  let query = qstring.parse(url.parse(req.url).query)
  if (query.ingredients || query.ingredient) {
    // Support the convenience query
    // We will replace "Basil" with "BMW" and "Cumin" with "Chevrolet"
    res.redirect("/?manufacturer=BMW,Chevrolet")
    res.end()
    return
  }
  
  if (query.manufacturer) {
    // Query database of cool cars based on specific manufacturer
    console.log("Query: " + query.manufacturer)
    
    // Use a closure to return the initial manufacturer that was requested
    getInitialManufacturer = function() {
      return query.manufacturer
    }
  }
  
  // Send the main application HTML file
  console.log("Sending main HTML file: " + INDEX_FILE)
  res.sendFile(file, err => {
    if (err) console.log(err)
    else res.end()
  })
})

function getInitialManufacturer() {
  return {}
}

app.post("/search", function(req, res) {
  var rawData = ""

  req.on("data", function(chunk) {
    rawData += chunk
  })

  req.on("end", function() {
    console.log(JSON.parse(rawData))

    let searchRequest = JSON.parse(rawData)
    var coolCars = {}
    if (!searchRequest.manufacturers) searchRequest.manufacturers = ""
    
    coolCars = getCoolCars(searchRequest.manufacturers, function(cars) {
      // console.log(cars)
      console.log("Sending cars")
      // Send the data to the client
      res.send(JSON.stringify(cars))
      res.end()
    })
  })
})

app.post("/load", function(req, res) {
  // Send the cars we initially loaded as part of the query in the url
  console.log("Sending initial cars loaded from url query")
  getCoolCars(getInitialManufacturer(), function(cars) {
    res.send(JSON.stringify(cars))
    res.end()
  })
})


app.listen(PORT, err => {
  if (err) console.log(err)
  else {
    console.log(`Server listening on port: ${PORT}`)
    console.log(`Using ${API_SERVER} as the API server`)
    console.log(`To test:`)
    console.log(`http://localhost:${PORT}`)
  }
})

