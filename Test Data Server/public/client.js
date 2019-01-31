function asyncAlert(message) {
  // Show hidden dialog to display message to user
  $("#dialog-message").html(message)
  $("#dialog").show()
}

function setupHandlebars(template) {
  // Setup car template
  var source = $("#car-template").get(0).innerHTML
  template.car = Handlebars.compile(source)
}

function updateUI(cars, template) {
  console.log("Updating cars")
  $("#content").empty()
  
  // Add content using handlebars template engine
  for (let car of cars) {

    // If this car doesn't have a "DougScore", hide the DougScore data
    if (!car.DougScore) {
      car.DougScore = "Not Available"
      car.HideClass = "car__data-hidden"
    } 

    // Evaluate the template and add it the page
    let html = template.car(car)
    $("#content").append(html)
  }
}

function setupUI(t) {
  $("#button__search").click(function onSearch() {
    console.log("Search button pressed")
    
    // Remove spaces, if any
    $("#input__manufacturer").val($("#input__manufacturer").val().split(" ").join(""))
    
    // Create request
    let request = {}

    request.manufacturers = $("#input__manufacturer").val()
    $.post("/search", JSON.stringify(request), function(rawData, status) {
      let template = t
      
      // Update UI with cars returned by request
      updateUI(JSON.parse(rawData), template)
    })
  })

  $("#dialog-close").click(function() {
    $("#dialog").hide();
  })
    
  // Setup initial UI with data from server (if any)
  console.log("Loading initial car request from query, if any")
  $.post("/load", function(rawData, status) {
    let template = t
    
    // Update UI with cars returned by request, if there are any
    console.log("Initial cars loaded")
    updateUI(JSON.parse(rawData), template)
  })
}

$(document).ready(function() {
  let template = {}
  setupHandlebars(template)
  setupUI(template)
  

  asyncAlert("This server allows you to search for cool cars. You can enter multiple comma seperated manufacturers.")
})
