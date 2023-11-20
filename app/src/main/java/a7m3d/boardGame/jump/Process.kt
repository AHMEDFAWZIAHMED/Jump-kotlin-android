package a7m3d.boardGame.jump

import android.widget.ImageView


object Process {

    val placeHoldersX = ArrayList<Float>()
    val placeHoldersY = ArrayList<Float>()
    val player1Index = ArrayList<Int>()
    val player2Index = ArrayList<Int>()
    val flatIndex = ArrayList<Int>()
    val jumpList = ArrayList<ArrayList<Int>>()
    val lastMove = ArrayList<Int>()
    private val colIndex = (0..4).map { col -> (0..4).map { it*5+col } }
    var playerAndIndex = arrayOf(0, 12)
    var playerTurn = 1
    var playersNumber = 1
    var difficulty = 1
    var countDown = 30
    var cDensity = 0f
    var cWidth = 0f
    var cMargin = 0f
    var defaultLang = true
    var cpuChange = true
    var langAndCont = arrayOf("ar", "SD")
    var imgSize = 0

    fun reset() {

        placeHoldersX.clear()
        placeHoldersY.clear()
        player1Index.clear()
        player2Index.clear()
        flatIndex.clear()
        jumpList.clear()
        if (jumpList.isNotEmpty()) {
            for (j in jumpList) j.clear()
        }
        lastMove.clear()
        playerAndIndex[0] = 0
        playerTurn = 1
        countDown = 30
    }

    private fun findNearNeighbors(number: Int): ArrayList<Int> {

        val allInd = listOf(-1, 1, -5, 5).map { it+number }
        val firstCol = listOf(1, -5, 5).map { it+number }
        val lastCol = listOf(-1, -5, 5).map { it+number }
        val nearNeighbors = ArrayList<Int>()

        when (number) {
            in colIndex[0] -> for (f in firstCol) if (f in flatIndex) nearNeighbors += f
            in colIndex[4] -> for (l in lastCol) if (l in flatIndex) nearNeighbors += l
            else -> for (a in allInd) if (a in flatIndex) nearNeighbors += a
        }
        return nearNeighbors
    }

    private fun findFarNeighbors(number: Int): ArrayList<Int> {

        val allInd = listOf(-2, 2, -10, 10).map { it+number }
        val firstTwoCol = listOf(2, -10, 10).map { it+number }
        val lastTwoCol = listOf(-2, -10, 10).map { it+number }
        val farNeighbors = ArrayList<Int>()

        when (number) {
            in colIndex[0], in colIndex[1] -> for (f in firstTwoCol) {
                if (f in flatIndex) farNeighbors += f
            }
            in colIndex[3], in colIndex[4] -> for (l in lastTwoCol) {
                if (l in flatIndex) farNeighbors += l
            }
            else -> for (a in allInd) if (a in flatIndex) farNeighbors += a
        }
        return farNeighbors
    }

    fun illegalMove(indx: Int): Boolean {

        return when (playerAndIndex[0]) {
            1 -> (indx !in possibleMove(playerAndIndex[1], player1Index, player2Index)) &&
                    (indx !in possibleJump(playerAndIndex[1], player1Index, player2Index))
            2 -> (indx !in possibleMove(playerAndIndex[1], player2Index, player1Index)) &&
                    (indx !in possibleJump(playerAndIndex[1], player2Index, player1Index))
            else -> true
        }
    }

    private fun possibleMove(num: Int, l1: ArrayList<Int>, l2: ArrayList<Int>): ArrayList<Int> {

        val moveIndex = ArrayList<Int>()
        for (neighbor in findNearNeighbors(l1[num])) {
            if (neighbor !in l1 && neighbor !in l2) moveIndex += neighbor
        }
        return moveIndex
    }

    private fun possibleJump(num: Int, l1: ArrayList<Int>, l2: ArrayList<Int>): ArrayList<Int> {

        jumpList.clear()
        if (jumpList.isNotEmpty()) {
            for (j in jumpList) j.clear()
        }
        val jumpIndex = ArrayList<Int>()
        for (n in findNearNeighbors(l1[num])) {
            if (n in l2) {
                for (f in findFarNeighbors(l1[num])) {
                    if (f !in l1 && f !in l2) {
                        if (f==n-1 || f==n+1 || f==n-5 || f==n+5) {
                            jumpIndex += f
                            jumpList.add(arrayListOf(f, l2.indexOf(n), num))
                        }
                    }
                }
            }
        }
        return jumpIndex
    }

    fun chngImgSize(img: ImageView, n1: Int, n2: Int) {

        val param = img.layoutParams
        param.width = imgSize*2*n2
        param.height = (imgSize*2)*n1*n2
        img.layoutParams = param
    }

    fun distance(plHoIn: Int, plrX: Float, plrY: Float): Int {

        var distanceX = (plrX - placeHoldersX[plHoIn]).toInt()
        if (distanceX < 0) distanceX *= -1
        var distanceY = (plrY - placeHoldersY[plHoIn]).toInt()
        if (distanceY < 0) distanceY *= -1
        return if (distanceX > distanceY) distanceX else distanceY
    }

    fun cpuChoice(rIndex: Int = 12): Int {

        val destinationMove = ArrayList<ArrayList<Int>>()
        val destinationJump = ArrayList<ArrayList<Int>>()
        val allJumps = ArrayList<ArrayList<ArrayList<Int>>>()
        val valueListDM = ArrayList<ArrayList<Int>>()
        val valueListDJ = ArrayList<ArrayList<Int>>()
        val indexDM = ArrayList<Int>()
        val indexDJ = ArrayList<Int>()
        val maxIndexDM = ArrayList<Int>()
        val maxIndexDJ = ArrayList<Int>()
        val maxValueDM = ArrayList<Int>()
        val maxValueDJ = ArrayList<Int>()
        val maxOfMaxV = ArrayList<Int>()
        val maxOfMaxI = ArrayList<Int>()
        val maxOfI = ArrayList<Int>()

        for (pIndx in 0..11) {
            destinationMove.add(arrayListOf())
            destinationJump.add(arrayListOf())
            allJumps.add(arrayListOf())
            valueListDM.add(arrayListOf(-2, -2, -2, -2))
            valueListDJ.add(arrayListOf(-2, -2, -2, -2))
            if (player2Index[pIndx] == -1) continue
            destinationMove[pIndx] += possibleMove(pIndx, player2Index, player1Index)
            destinationJump[pIndx] += possibleJump(pIndx, player2Index, player1Index)
            allJumps[pIndx] += jumpList
        }

        fun targeted(rIndx: Int, pl1: ArrayList<Int>, pl2: ArrayList<Int>): Boolean {

            for (n in findNearNeighbors(rIndx)) {
                if (n in pl1 && possibleJump(pl1.indexOf(n),
                        pl1, pl2).isNotEmpty()) {
                    for (j in jumpList) {
                        if (j[1] == rIndx) {
                            return true
                        }
                    }
                }
            }
            return false
        }

        fun valuation(indxNum: Int, distNum: Int, jump: Boolean): Int {

            val plr1IndxCopy = ArrayList(player1Index)
            val plr2IndxCopy = ArrayList(player2Index)
            var value = 10
            var pl1Indx = -1
            var pl1Val = -1

            plr2IndxCopy[indxNum] = distNum

            if (jump) {
                for (aJ in allJumps[indxNum]) {
                    if (aJ[0] == distNum) plr1IndxCopy[aJ[1]] = -1
                }
                for (n in findNearNeighbors(distNum)) {
                    if (n in plr1IndxCopy && possibleJump(indxNum,
                            plr2IndxCopy, plr1IndxCopy).isNotEmpty()) {
                        for (j in jumpList) {
                            if (j[1] == plr1IndxCopy.indexOf(n)) {
                                plr1IndxCopy[j[1]] = -1
                                plr2IndxCopy[indxNum] = j[0]
                                value += 4
                            }
                        }
                    }
                }
            }
            else {
                for (num in 0..11) {
                    if (num == indxNum) continue
                    if (targeted(num, plr1IndxCopy, plr2IndxCopy)) value--
                }
            }

            if (difficulty == 1) return value

            outer@for (n in findNearNeighbors(plr2IndxCopy[indxNum])) {
                if (n in plr1IndxCopy && possibleJump(plr1IndxCopy.indexOf(n),
                        plr1IndxCopy, plr2IndxCopy).isNotEmpty()) {
                    for (j in jumpList) {
                        if (j[1] == indxNum) {
                            plr2IndxCopy[j[1]] = -1
                            plr1IndxCopy[plr1IndxCopy.indexOf(n)] = j[0]
                            pl1Indx = plr1IndxCopy.indexOf(j[0])
                            pl1Val = j[0]
                            value -= 3
                            break@outer
                        }
                    }
                }
            }
            if (pl1Indx > -1) {
                outer@for (n in findNearNeighbors(pl1Val)) {
                    if (n in plr2IndxCopy && possibleJump(plr2IndxCopy.indexOf(n),
                            plr2IndxCopy, plr1IndxCopy).isNotEmpty()) {
                        for (j in jumpList) {
                            if (j[1] == pl1Indx) {
                                value += 2
                                break@outer
                            }
                        }
                    }
                }
                outer@for (n in findNearNeighbors(pl1Val)) {
                    if (n in plr2IndxCopy && possibleJump(pl1Indx,
                            plr1IndxCopy, plr2IndxCopy).isNotEmpty()) {
                        for (j in jumpList) {
                            if (j[0] == pl1Val) {
                                value -= 3
                                break@outer
                            }
                        }
                    }
                }
            }

            return value
        }

        for (i in 0..11) {
            if (destinationMove[i].isNotEmpty()) {
                for (dMIndx in 0 until destinationMove[i].size) {
                    valueListDM[i][dMIndx] += valuation(i, destinationMove[i][dMIndx], false)
                    if (targeted(i, player1Index, player2Index)) valueListDM[i][dMIndx] += 1
                }
            }
            if (destinationJump[i].isNotEmpty()) {
                for (dJIndx in 0 until destinationJump[i].size) {
                    valueListDJ[i][dJIndx] += valuation(i, destinationJump[i][dJIndx], true)
                    if (targeted(i, player1Index, player2Index)) valueListDJ[i][dJIndx] += 1
                    if (i == rIndex) valueListDJ[i][dJIndx] += 5
                }
            }
        }
        for ((iM, vM) in valueListDM.withIndex()) {
            if (vM.max() == -2) continue
            for ((i, v) in vM.withIndex()) {
                if (v == vM.max()) {
                    maxValueDM += v
                    maxIndexDM += i
                    indexDM += iM
                }
            }
        }
        for ((iJ, vJ) in valueListDJ.withIndex()) {
            if (vJ.max() == -2) continue
            for ((i, v) in vJ.withIndex()) {
                if (v == vJ.max()) {
                    maxValueDJ += v
                    maxIndexDJ += i
                    indexDJ += iJ
                }
            }
        }
        if (indexDJ.isNotEmpty()) {
            for ((i, v) in maxValueDJ.withIndex()) {
                if (v == maxValueDJ.max()) {
                    maxOfMaxV.add(v)
                    maxOfMaxI.add(maxIndexDJ[i])
                    maxOfI.add(indexDJ[i])
                }
            }
            if (rIndex != 12 && rIndex !in maxOfI) return -1
            val indx = (maxOfMaxV.indices).shuffled().last()
            playerAndIndex[0] = 2
            playerAndIndex[1] = maxOfI[indx]
            jumpList.clear()
            if (jumpList.isNotEmpty()) {
                for (j in jumpList) j.clear()
            }
            jumpList += allJumps[maxOfI[indx]]
            return destinationJump[maxOfI[indx]][maxOfMaxI[indx]]
        }
        else if (indexDM.isNotEmpty() && rIndex == 12) {
            for ((i, v) in maxValueDM.withIndex()) {
                if (v == maxValueDM.max()) {
                    maxOfMaxV.add(v)
                    maxOfMaxI.add(maxIndexDM[i])
                    maxOfI.add(indexDM[i])
                }
            }
            val indx = (maxOfMaxV.indices).shuffled().last()
            playerAndIndex[0] = 2
            playerAndIndex[1] = maxOfI[indx]
            jumpList.clear()
            if (jumpList.isNotEmpty()) {
                for (j in jumpList) j.clear()
            }
            jumpList += allJumps[maxOfI[indx]]
            return destinationMove[maxOfI[indx]][maxOfMaxI[indx]]
        }
        else return -1
    }
}






