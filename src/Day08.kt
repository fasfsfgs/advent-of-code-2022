fun main() {
    fun part1(forest: Forest): Int {
        var visibleTrees = 0

        for (row in 0..forest.rows.lastIndex)
            for (column in 0..forest.rows[0].lastIndex)
                if (forest.isTreeVisible(row, column)) visibleTrees++

        return visibleTrees
    }

    fun part2(forest: Forest): Int {
        var highestScenicScore = 0

        for (row in 0..forest.rows.lastIndex)
            for (column in 0..forest.rows[0].lastIndex)
                highestScenicScore = maxOf(highestScenicScore, forest.getScenicScore(row, column))

        return highestScenicScore
    }

    val testForest = readInput("Day08_test").toForest()
    check(part1(testForest) == 21)
    check(testForest.getScenicScore(1, 2) == 4)
    check(part2(testForest) == 8)

    val forest = readInput("Day08").toForest()
    println(part1(forest))
    println(part2(forest))
}

fun List<String>.toForest() = Forest(map { line -> line.map { it.toString().toInt() } })

data class Forest(val rows: List<List<Int>>) {

    fun isTreeVisible(row: Int, column: Int): Boolean {
        if (row == 0 || column == 0 || row == rows.lastIndex || column == rows[0].lastIndex) return true
        return isVisibleFromLeft(row, column)
                || isVisibleFromRight(row, column)
                || isVisibleFromTop(row, column)
                || isVisibleFromBottom(row, column)
    }

    private fun isVisibleFromLeft(row: Int, column: Int) =
        (0 until column).all { rows[row][it] < rows[row][column] }

    private fun isVisibleFromRight(row: Int, column: Int) =
        (rows[0].lastIndex downTo column + 1).all { rows[row][it] < rows[row][column] }

    private fun isVisibleFromTop(row: Int, column: Int) =
        (0 until row).all { rows[it][column] < rows[row][column] }

    private fun isVisibleFromBottom(row: Int, column: Int) =
        (rows.lastIndex downTo row + 1).all { rows[it][column] < rows[row][column] }

    fun getScenicScore(row: Int, column: Int): Int {
        if (row == 0 || column == 0 || row == rows.lastIndex || column == rows[0].lastIndex) return 0
        val left = getScenicScoreFromLeft(row, column)
        val right = getScenicScoreFromRight(row, column)
        val top = getScenicScoreFromTop(row, column)
        val bottom = getScenicScoreFromBottom(row, column)
        return left * right * top * bottom
    }

    private fun getScenicScoreFromLeft(row: Int, column: Int): Int =
        getScenicScore(row, column, column - 1 downTo 0) { rows[row][it] }

    private fun getScenicScoreFromRight(row: Int, column: Int): Int =
        getScenicScore(row, column, column + 1..rows[0].lastIndex) { rows[row][it] }

    private fun getScenicScoreFromTop(row: Int, column: Int): Int =
        getScenicScore(row, column, row - 1 downTo 0) { rows[it][column] }

    private fun getScenicScoreFromBottom(row: Int, column: Int): Int =
        getScenicScore(row, column, row + 1..rows.lastIndex) { rows[it][column] }

    private fun getScenicScore(row: Int, column: Int, range: IntProgression, treeToCheck: (Int) -> Int): Int {
        var visibleTrees = 0
        for (i in range) {
            visibleTrees++
            if (treeToCheck(i) >= rows[row][column]) break
        }
        return visibleTrees
    }

}
