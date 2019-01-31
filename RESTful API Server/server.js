const sqlite3 = require("sqlite3").verbose()
const express = require("express")
const app = express()
const url = require("url")
const qstring = require("querystring")

const PORT = process.env.PORT || 3001

const ROOT_DIR = "/public"

function databaseDebug(db) {
  // Prints all records in cool cars table in database
  console.log("Logging database entries...")

  db.all("SELECT * FROM CoolCars", [], (err, records) => {
    records.forEach((record) => {
      console.log(`\n${record.Manufacturer} ${record.Model}\n  Engine: ${record.EngineType}\n  Layout: ${record.Layout}\n  First Introduced: ${record.YearIntroduced}`)
    })
  })
}

// Static file service
app.use(express.static(__dirname + ROOT_DIR))

app.get('/getMakes', (req, res) => {
  //Parse query
  let jQuery = qstring.parse(url.parse(req.url).query)
  res.writeHead(200)
  
  console.log("Getting all available makes")
  
  let sql = `SELECT DISTINCT Manufacturer FROM CoolCars`
  db.all(sql, (err, records) => {
    console.log(records)
    
    // Turn request into simple array
    var array = []
    for (record of records) {
      array.push(record.Manufacturer)
    }
    
    res.write(JSON.stringify({makes: array}))
    res.end()
  })
})

app.get('/getCars', (req, res) => { 
  // Parse query
  let query = qstring.parse(url.parse(req.url).query)
  res.writeHead(200)

  if (query.manufacturer) {
    // Query database of cool cars based on specific manufacturer
    console.log(query.manufacturer)
    
    // Create a sanitized SQL query to get multiple manufacturers
    // Use COLLATE NOCASE to ensure that capitalization doesn't matter
    let sanitizedInputs = query.manufacturer.split(",")
    let sql = `SELECT * FROM CoolCars WHERE Manufacturer=@0 COLLATE NOCASE`
    for (let i = 1; i < sanitizedInputs.length; i++) {
      sql += ` OR Manufacturer=@${i}`
      sql += " COLLATE NOCASE"
    }

    console.log("Complex query = " + sql + "\n" + sanitizedInputs)

    db.all(sql, sanitizedInputs, (err, records) => {
      res.write(JSON.stringify(records))
      res.end()
    })
    
  } else {
    // If no specific manufacturer is queried, return all records
    db.all("SELECT * FROM CoolCars", [], (err, records) => {
      res.write(JSON.stringify(records))
      res.end()
    })
  }
})

db = new sqlite3.Database(__dirname + "/database.db", (err) => {
  if (err) {
    console.log(err.message)
  }

  console.log("Connected to database successfully")

  // If you uncomment this, it will print out each DB record to console
  //databaseDebug(db)
})

app.listen(PORT, err => {
  if (err) console.log(err)
  else {
    console.log(`Server listening on port: ${PORT}`)
    console.log(`To connect to API: localhost:${PORT}/api`)
  }
})
