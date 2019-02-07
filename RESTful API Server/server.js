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

app.get('/api/getMakes', (req, res) => {
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

app.get('/api/getCars', (req, res) => { 
  // Parse query
  let query = qstring.parse(url.parse(req.url).query)
  res.writeHead(200)

  if (query.manufacturer || query.minPrice || query.maxPrice || query.minYear || query.maxYear) {
    
    let i = 0;
    let sql = `SELECT * FROM CoolCars WHERE `
    var sanitizedInputs = []
    
    if (query.manufacturer) {
      // Create a sanitized SQL query to get multiple manufacturers
      // Use COLLATE NOCASE to ensure that capitalization doesn't matter
      for (var m of query.manufacturer.split(",")) sanitizedInputs.push(m)
      i = 1
      sql += `(Manufacturer=@0 COLLATE NOCASE`
      for (; i < sanitizedInputs.length; i++) {
        sql += ` OR Manufacturer=@${i}`
        sql += `COLLATE NOCASE`
      }
      sql += `)`
    }
    
    // Query for year
    if (query.minYear) {
      if (i != 0) sql += ` AND `
      sql += `Year >= @${i++}`
      sanitizedInputs.push(query.minYear);
    }
    if (query.maxYear) {
      if (i != 0) sql += ` AND `
      sql += `Year <= @${i++}`
      sanitizedInputs.push(query.maxYear);
    }
    
    // Query for price
    if (query.minPrice) {
      if (i != 0) sql += ` AND `
      sql += `ListPrice >= @${i++}`
      sanitizedInputs.push(query.minPrice);
    }
    if (query.maxPrice) {
      if (i != 0) sql += ` AND `
      sql += `ListPrice <= @${i++}`
      sanitizedInputs.push(query.maxPrice);
    }
    sql += `;`
    
    console.log("\nSQL query: \n" + sql + "\n")
    console.log("Sanitized inputs : \n" + sanitizedInputs + "\n")

    db.all(sql, sanitizedInputs, (err, records) => {
      console.log("SQL Query returned: \n" + JSON.stringify(records) + "\n")
      res.write(JSON.stringify(records))
      res.end()
    })
    
  } else {
    // If no specific manufacturer is queried, return all records
    let sql = "SELECT * FROM CoolCars"
    console.log("\nSQL query: \n" + sql + "\n")
    db.all(sql, [], (err, records) => {
      console.log("SQL Query returned: \n" + JSON.stringify(records) + "\n")
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
