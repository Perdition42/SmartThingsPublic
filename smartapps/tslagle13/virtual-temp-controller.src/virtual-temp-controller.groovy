definition(
    name: "Virtual Temp Controller",
    namespace: "tslagle13",
    author: "Tim Slagle/Chuck Tommervik",
    category: "Green Living",
    description: "Turns on a switch when temp gets too high, back off when it reaches the target again.",
    iconUrl: "http://icons.iconarchive.com/icons/icons8/windows-8/512/Science-Temperature-icon.png",
    iconX2Url: "http://icons.iconarchive.com/icons/icons8/windows-8/512/Science-Temperature-icon.png"
)

preferences {
    section("Humidity") {
        input "tempSensor", "capability.temperatureMeasurement", title: "Which Sensor?"
        input "desiredTemperature", "number", title: "Desired Temperature?"
        input "tempSwitch", "capability.switch", title: "Which Switch?"
    }
}

def installed() {
    initialize()
}

def updated() {
    unsubscribe()
    initialize()
}

def initialize() {
    subscribe(tempSensor, "temperature", tempHandler)
    log.debug "Initialized... current temp is ${tempSensor.latestValue("temp")}%, max temp is ${desiredTemperature*1.05}, tempSwitch is ${tempSwitch.latestValue( "switch" )}"
    tempSwitch.refresh()             // Update power display
}


def tempHandler(evt) {
    log.debug "temp: $evt.value, $evt"

    if (Double.parseDouble(evt.value.replace("%", "")) <= desiredTemperature) {
        if ( tempSwitch.latestValue( "switch" ) != "off" ) {
            log.debug "Turning ${tempSwitch} on"
            tempSwitch.off()
        }
    }
    else if (Double.parseDouble(evt.value.replace("%", "")) > desiredTemperature ) {
        if ( tempSwitch.latestValue( "switch" ) != "on" ) {
            log.debug "Turning ${tempSwitch} on"
            tempSwitch.on()
        }  
    }
    else {
        log.debug "Current temp is ${evt.value}"
    }
    tempSwitch.poll()                // every time the temp changes, poll the switch for power updates
}