fun main() {
    fun part1(monkeys: List<Monkey>): Long {
        return play(monkeys, 20) { it / 3 }
    }

    fun part2(monkeys: List<Monkey>): Long {
        val cd = monkeys.map { it.test }.reduce(Long::times)
        return play(monkeys, 10_000) { it % cd }
    }

    check(part1(getTestMonkeys()) == 10605L)
    println(part1(getMonkeys()))

    check(part2(getTestMonkeys()) == 2713310158L)
    println(part2(getMonkeys()))
}

fun play(monkeys: List<Monkey>, rounds: Int, worryMgmt: (Long) -> Long): Long {
    repeat(rounds) {
        monkeys.forEach { it.turn(monkeys, worryMgmt) }
    }

    val (highest, secondHighest) = monkeys
        .map { it.inspectionCounter }
        .sortedDescending()
        .take(2)

    return highest * secondHighest
}

data class Monkey(
    val items: ArrayDeque<Long>,
    val operation: (Long) -> Long,
    val test: Long,
    val trueMonkey: Int,
    val falseMonkey: Int
) {

    var inspectionCounter = 0L

    fun turn(monkeys: List<Monkey>, worryMgmt: (Long) -> Long) {
        while (items.isNotEmpty()) {
            inspectionCounter++

            var item = items.removeFirst()
            item = operation(item)

            item = worryMgmt(item)

            if (item % test == 0L)
                monkeys[trueMonkey].catch(item)
            else
                monkeys[falseMonkey].catch(item)
        }
    }

    private fun catch(item: Long) = items.addLast(item)

}

fun getTestMonkeys(): List<Monkey> {
    val monkey0 = Monkey(
        items = ArrayDeque(listOf(79L, 98L)),
        operation = { old -> old * 19L },
        test = 23L,
        trueMonkey = 2,
        falseMonkey = 3
    )

    val monkey1 = Monkey(
        items = ArrayDeque(listOf(54L, 65L, 75L, 74L)),
        operation = { old -> old + 6L },
        test = 19L,
        trueMonkey = 2,
        falseMonkey = 0
    )

    val monkey2 = Monkey(
        items = ArrayDeque(listOf(79L, 60L, 97L)),
        operation = { old -> old * old },
        test = 13L,
        trueMonkey = 1,
        falseMonkey = 3
    )

    val monkey3 = Monkey(
        items = ArrayDeque(listOf(74L)),
        operation = { old -> old + 3L },
        test = 17L,
        trueMonkey = 0,
        falseMonkey = 1
    )

    return listOf(monkey0, monkey1, monkey2, monkey3)
}

fun getMonkeys(): List<Monkey> {
    val monkey0 = Monkey(
        items = ArrayDeque(listOf(99L, 63L, 76L, 93L, 54L, 73L)),
        operation = { old -> old * 11L },
        test = 2L,
        trueMonkey = 7,
        falseMonkey = 1
    )

    val monkey1 = Monkey(
        items = ArrayDeque(listOf(91L, 60L, 97L, 54L)),
        operation = { old -> old + 1L },
        test = 17L,
        trueMonkey = 3,
        falseMonkey = 2
    )

    val monkey2 = Monkey(
        items = ArrayDeque(listOf(65L)),
        operation = { old -> old + 7L },
        test = 7L,
        trueMonkey = 6,
        falseMonkey = 5
    )

    val monkey3 = Monkey(
        items = ArrayDeque(listOf(84L, 55L)),
        operation = { old -> old + 3L },
        test = 11L,
        trueMonkey = 2,
        falseMonkey = 6
    )

    val monkey4 = Monkey(
        items = ArrayDeque(listOf(86L, 63L, 79L, 54L, 83L)),
        operation = { old -> old * old },
        test = 19L,
        trueMonkey = 7,
        falseMonkey = 0
    )

    val monkey5 = Monkey(
        items = ArrayDeque(listOf(96L, 67L, 56L, 95L, 64L, 69L, 96L)),
        operation = { old -> old + 4L },
        test = 5L,
        trueMonkey = 4,
        falseMonkey = 0
    )

    val monkey6 = Monkey(
        items = ArrayDeque(listOf(66L, 94L, 70L, 93L, 72L, 67L, 88L, 51L)),
        operation = { old -> old * 5L },
        test = 13L,
        trueMonkey = 4,
        falseMonkey = 5
    )

    val monkey7 = Monkey(
        items = ArrayDeque(listOf(59L, 59L, 74L)),
        operation = { old -> old + 8L },
        test = 3L,
        trueMonkey = 1,
        falseMonkey = 3
    )

    return listOf(monkey0, monkey1, monkey2, monkey3, monkey4, monkey5, monkey6, monkey7)
}
