import kotlin.math.abs

fun main() {
    fun part1(input: List<String>, desiredY: Int): Int {
        val report = input.toReport()

        val scannedXAtY = report
            .map { it.sensor }
            .flatMap { it.xScannedAtY(desiredY) }
            .distinct()

        val xWithBeaconAtY = report
            .filter { it.beacon.y == desiredY }
            .map { it.beacon.x }

        return scannedXAtY.filter { it !in xWithBeaconAtY }.size
    }

    fun part2(input: List<String>, maxRange: Int): Int {
        return input.size
    }

    val testInput = readInput("Day15_test")
    check(part1(testInput, 10) == 26)
//    check(part2(testInput, 20) == 56_000_011)

    val input = readInput("Day15")
    println(part1(input, 2_000_000))
//    println(part2(input, 4_000_000))
}

data class Sensor(val x: Int, val y: Int, val radius: Int) {

    companion object {
        fun withBeacon(x: Int, y: Int, nearestBeacon: Beacon): Sensor {
            val (beaconX, beaconY) = nearestBeacon
            val deltaX = abs(x - beaconX)
            val deltaY = abs(y - beaconY)
            return Sensor(x, y, deltaX + deltaY)
        }
    }

    fun xScannedAtY(desiredY: Int): List<Int> {
        if (desiredY !in y - radius..y + radius) return listOf<Int>()

        val usedRadiusToGetToDesiredY = abs(y - desiredY)
        val radiusLeft = radius - usedRadiusToGetToDesiredY

        return (x - radiusLeft..x + radiusLeft).toList()
    }

}

data class Beacon(val x: Int, val y: Int)

data class Report(val sensor: Sensor, val beacon: Beacon)

fun List<String>.toReport(): List<Report> = map {
    val (strSensor, strBeacon) = it.split(":")
    val sensorX = strSensor.substringAfter("=").substringBefore(",").toInt()
    val sensorY = strSensor.substringAfterLast("=").toInt()
    val beaconX = strBeacon.substringAfter("=").substringBefore(",").toInt()
    val beaconY = strBeacon.substringAfterLast("=").toInt()
    val beacon = Beacon(beaconX, beaconY)
    val sensor = Sensor.withBeacon(sensorX, sensorY, beacon)
    Report(sensor, beacon)
}
