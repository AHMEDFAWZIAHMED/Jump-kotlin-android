package a7m3d.boardGame.jump

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.*
import androidx.annotation.RequiresApi
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.roundToInt

class StartGame : Activity(), DialogInterface.OnClickListener {

    private val rowsList = ArrayList<LinearLayout>()
    private val placeHolders = ArrayList<ImageView>()
    private val player1List = ArrayList<ImageView>()
    private val player2List = ArrayList<ImageView>()
    private lateinit var fLayout: FrameLayout
    private lateinit var board: LinearLayout
    private lateinit var endTurn1: ImageView
    private lateinit var endTurn2: ImageView
    private lateinit var undo1: ImageView
    private lateinit var undo2: ImageView
    private lateinit var player1: ImageView
    private lateinit var player2: ImageView
    private lateinit var msg: TextView
    private lateinit var popup: PopupWindow
    private lateinit var rowsID: List<Int>
    private lateinit var placeHoldersID: List<Int>

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        actionBar?.hide()
        window.addFlags(1024)
        val locale = Locale(Process.langAndCont[0], Process.langAndCont[1])
        Locale.setDefault(locale)
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N) {
            val config = baseContext.resources.configuration
            config.setLocale(locale)
            baseContext.createConfigurationContext(config).resources
            baseContext.resources.configuration.setToDefaults()
        }
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.N) {
            val config1 = Configuration()
            config1.locale = locale
            baseContext.resources.updateConfiguration(config1, baseContext.resources.displayMetrics)
        }
        setContentView(R.layout.activity_start_game)

        fLayout = findViewById(R.id.frame)
        board = findViewById(R.id.board)

        rowsID = listOf(R.id.row0, R.id.row1, R.id.row2, R.id.row3, R.id.row4)

        for (l in 0..4) {
            val linearL = findViewById<LinearLayout>(rowsID[l])
            rowsList.add(linearL)
        }

        placeHoldersID = listOf(R.id.holder0, R.id.holder1, R.id.holder2, R.id.holder3,
            R.id.holder4, R.id.holder5, R.id.holder6, R.id.holder7, R.id.holder8, R.id.holder9,
            R.id.holder10, R.id.holder11, R.id.holder12, R.id.holder13, R.id.holder14, R.id.holder15,
            R.id.holder16, R.id.holder17, R.id.holder18, R.id.holder19, R.id.holder20, R.id.holder21,
            R.id.holder22, R.id.holder23, R.id.holder24)

        var locXY = 0
        val pMargin = (Process.cMargin * Process.cDensity).roundToInt()
        Handler(Looper.getMainLooper()).postDelayed({
            for (i in 0..24) {
                val plHol = findViewById<ImageView>(placeHoldersID[i])
                val param = plHol.layoutParams as ViewGroup.MarginLayoutParams
                param.setMargins(pMargin, pMargin, pMargin, pMargin)
                param.height = Process.imgSize
                param.width = Process.imgSize
                plHol.layoutParams = param
                placeHolders.add(plHol)
                Process.flatIndex.add(i)
                plHol.setOnClickListener {
                    moveRock(i)
                }
                Handler(Looper.getMainLooper()).postDelayed({
                    if (i == 5 || i == 10 || i == 15 || i == 20) locXY++
                    Process.placeHoldersX.add(board.x + rowsList[locXY].x + plHol.x + locXY)
                    Process.placeHoldersY.add(board.y + rowsList[locXY].y + plHol.y + (locXY+1))
                }, 20)
            }
        }, 20)

        player1 = findViewById(R.id.player1)
        player2 = findViewById(R.id.player2)
        if (Process.defaultLang) {
            player1.setImageResource(R.drawable.player1en192)
            player2.setImageResource(R.drawable.player2en192)
        }else {
            player1.setImageResource(R.drawable.player1ar192)
            player2.setImageResource(R.drawable.player2ar192)
        }
        Process.chngImgSize(player1, 2, 1)
        Process.chngImgSize(player2, 2, 1)
        player2.setColorFilter(R.color.black)

        endTurn1 = findViewById(R.id.p1)
        endTurn2 = findViewById(R.id.p2)
        endTurn1.setOnClickListener { endTurn(1) }
        endTurn2.setOnClickListener { endTurn(2) }
        if (Process.defaultLang) {
            endTurn1.setImageResource(R.drawable.endturn192en1)
            endTurn2.setImageResource(R.drawable.endturn192en2)
        }else {
            endTurn1.setImageResource(R.drawable.endturn192ar1)
            endTurn2.setImageResource(R.drawable.endturn192ar2)
        }
        Process.chngImgSize(endTurn1, 1, 1)
        Process.chngImgSize(endTurn2, 1, 1)
        endTurn1.setColorFilter(R.color.black)
        endTurn2.setColorFilter(R.color.black)

        undo1 = findViewById(R.id.undo1)
        undo2 = findViewById(R.id.undo2)
        Process.chngImgSize(undo1, 1, 1)
        Process.chngImgSize(undo2, 1, 1)
        undo1.setColorFilter(R.color.black)
        undo2.setColorFilter(R.color.black)
        undo1.setOnClickListener { undo(1) }
        undo2.setOnClickListener { undo(2) }

        msg = TextView(this)
        msg.setTextColor(Color.WHITE)
        msg.textSize = Process.cWidth/2.5f
        //msg.rotation = 180f
        popup = PopupWindow(msg, WRAP_CONTENT, WRAP_CONTENT)

        Handler(Looper.getMainLooper()).postDelayed({createViews()}, 530)
    }

    private fun createViews() {

        for (i in 0..11) {
            val rock2 = ImageView(this)
            rock2.setImageResource(R.drawable.rocktwo192)
            rock2.layoutParams = ViewGroup.LayoutParams(Process.imgSize, Process.imgSize)
            rock2.x = Process.placeHoldersX[i]
            rock2.y = Process.placeHoldersY[i]
            if (Process.playersNumber == 2) {
                rock2.setOnClickListener { rockSelection(2, i, player2List) }
            }
            Process.player2Index.add(i)
            player2List.add(rock2)
            fLayout.addView(rock2)
        }

        for (i in 13..24) {
            val rock1 = ImageView(this)
            rock1.setImageResource(R.drawable.rockone192)
            rock1.layoutParams = ViewGroup.LayoutParams(Process.imgSize, Process.imgSize)
            rock1.x = Process.placeHoldersX[i]
            rock1.y = Process.placeHoldersY[i]
            rock1.setOnClickListener { rockSelection(1, i-13, player1List) }
            Process.player1Index.add(i)
            player1List.add(rock1)
            fLayout.addView(rock1)
        }
    }

    private fun undo(numb: Int) {

        if (Process.lastMove.isEmpty() || Process.playerTurn != numb) {
            Toast.makeText(this, "Last move not found!", Toast.LENGTH_SHORT).show()
            return
        }
        val laMo = Process.lastMove
        when (numb) {
            1 -> {
                if (laMo.size == 2) {
                    Process.player1Index[laMo[0]] = laMo[1]
                    player1List[laMo[0]].x = Process.placeHoldersX[laMo[1]]
                    player1List[laMo[0]].y = Process.placeHoldersY[laMo[1]]
                    Process.lastMove.clear()
                }
                else {
                    Process.player1Index[laMo[laMo.size-4]] = laMo[laMo.size-3]
                    player1List[laMo[laMo.size-4]].x = Process.placeHoldersX[laMo[laMo.size-3]]
                    player1List[laMo[laMo.size-4]].y = Process.placeHoldersY[laMo[laMo.size-3]]
                    player2List[laMo[laMo.size-2]].visibility = View.VISIBLE
                    Process.player2Index[laMo[laMo.size-2]] = laMo[laMo.size-1]
                }
                if (laMo.size <= 4) {
                    endTurn1.isEnabled = false
                    endTurn1.setColorFilter(R.color.black)
                }
                undo1.isEnabled = false
                undo1.setColorFilter(R.color.black)
            }
            2 -> {
                if (laMo.size == 2) {
                    Process.player2Index[laMo[0]] = laMo[1]
                    player2List[laMo[0]].x = Process.placeHoldersX[laMo[1]]
                    player2List[laMo[0]].y = Process.placeHoldersY[laMo[1]]
                    Process.lastMove.clear()
                }
                else {
                    Process.player2Index[laMo[laMo.size-4]] = laMo[laMo.size-3]
                    player2List[laMo[laMo.size-4]].x = Process.placeHoldersX[laMo[laMo.size-3]]
                    player2List[laMo[laMo.size-4]].y = Process.placeHoldersY[laMo[laMo.size-3]]
                    player1List[laMo[laMo.size-2]].visibility = View.VISIBLE
                    Process.player1Index[laMo[laMo.size-2]] = laMo[laMo.size-1]
                }
                if (laMo.size <= 4) {
                    endTurn2.isEnabled = false
                    endTurn2.setColorFilter(R.color.black)
                }
                undo2.isEnabled = false
                undo2.setColorFilter(R.color.black)
            }
        }
    }

    private fun endTurn(num: Int) {

        if (Process.playerTurn != num) return
        if (Process.playerAndIndex[0] != 0) {
            if (player().alpha == 0.9f) {
                player().alpha = 1f
                player().clearColorFilter()
                return
            }
        }
        val rockRemain1 = Process.player1Index.toSet()
        val rockRemain2 = Process.player2Index.toSet()
        if (rockRemain1.size < 7 || rockRemain2.size < 7) {
            if (Process.playerTurn == 1) {
                Process.countDown--
                showCount()
            }
        }

        when (num) {
            1 -> {
                if (Process.playersNumber == 1 && Process.playerAndIndex[0] == 1) {
                    Process.lastMove.clear()
                    Process.playerTurn = 2
                    endTurn1.setColorFilter(R.color.black)
                    undo1.setColorFilter(R.color.black)
                    player1.setColorFilter(R.color.black)
                    player2.clearColorFilter()
                    Handler(Looper.getMainLooper()).postDelayed(
                        { moveRock(Process.cpuChoice()) },
                        200
                    )
                } else if (Process.playerAndIndex[0] == 1) {
                    Process.lastMove.clear()
                    Process.playerTurn = 2
                    endTurn1.setColorFilter(R.color.black)
                    undo1.setColorFilter(R.color.black)
                    player1.setColorFilter(R.color.black)
                    player2.clearColorFilter()
                }
            }
            else -> {
                if (Process.playersNumber == 2 && Process.playerAndIndex[0] == 2) {
                    Process.lastMove.clear()
                    Process.playerTurn = 1
                    endTurn2.setColorFilter(R.color.black)
                    undo2.setColorFilter(R.color.black)
                    player2.setColorFilter(R.color.black)
                    player1.clearColorFilter()
                }
            }
        }
    }

    private fun rockSelection(plNum: Int, index: Int, pList: ArrayList<ImageView>) {

        if (Process.playerAndIndex[0] != 0) {
            if (player().alpha == 0.9f) {
                player().alpha = 1f
                player().clearColorFilter()
            }
        }
        if (Process.playerTurn != plNum) {
            Toast.makeText(this, "Not your turn!", Toast.LENGTH_SHORT).show()
            return
        }
        if (Process.lastMove.isNotEmpty()) {
            if (Process.lastMove.size == 2) {
                Toast.makeText(this, "No move left!", Toast.LENGTH_SHORT).show()
                return
            }
            else if (index != Process.lastMove[0]) {
                Toast.makeText(this, "Only double jumps!", Toast.LENGTH_SHORT).show()
                return
            }
        }
        Process.playerAndIndex[0] = plNum
        Process.playerAndIndex[1] = index
        pList[index].setColorFilter(R.color.black)
        pList[index].alpha = 0.9f
    }

    private fun moveRock(placeHolderIndx: Int) {

        if (Process.playerAndIndex[0] != Process.playerTurn) return
        if (Process.lastMove.isNotEmpty() && !Process.illegalMove(placeHolderIndx)) {
            if (Process.jumpList.isNotEmpty()) {
                var noJump = true
                for (j in Process.jumpList) {
                    if (j[0] == placeHolderIndx) noJump = false
                }
                if (noJump) {
                    Toast.makeText(this, "Only double jumps!",
                                    Toast.LENGTH_SHORT).show()
                    return
                }
            }else {
                Toast.makeText(this, "No move left!", Toast.LENGTH_SHORT).show()
                return
            }
        }
        enableAllLabels(false)
        if (Process.illegalMove(placeHolderIndx)) {
            if (Process.playerAndIndex[0] != 0) {
                if (player().alpha == 0.9f) {
                    player().alpha = 1f
                    player().clearColorFilter()
                    Toast.makeText(this, "Cannot move here!", Toast.LENGTH_SHORT).show()
                }
            }
            enableAllLabels(true)
            return
        }
        player().alpha = 1f
        player().clearColorFilter()
        val animateRock = ImageView(this)
        animateRock.setImageDrawable(player().drawable)
        animateRock.layoutParams = ViewGroup.LayoutParams(Process.imgSize, Process.imgSize)
        animateRock.x = player().x
        animateRock.y = player().y
        fLayout.addView(animateRock)
        player().visibility = View.INVISIBLE
        val distance = Process.distance(placeHolderIndx, player().x, player().y)
        for (t in 1..distance) {
            if (animateRock.x == Process.placeHoldersX[placeHolderIndx]
                && animateRock.y == Process.placeHoldersY[placeHolderIndx]) break
            Handler(Looper.getMainLooper()).postDelayed({
                if (animateRock.x < Process.placeHoldersX[placeHolderIndx]) {
                    animateRock.x++
                }
                if (animateRock.x > Process.placeHoldersX[placeHolderIndx]) {
                    animateRock.x--
                }
                if (animateRock.y < Process.placeHoldersY[placeHolderIndx]) {
                    animateRock.y++
                }
                if (animateRock.y > Process.placeHoldersY[placeHolderIndx]) {
                    animateRock.y--
                }
            }, (t*5/Process.cDensity).toLong())
        }

        Handler(Looper.getMainLooper()).postDelayed({
            player().x = Process.placeHoldersX[placeHolderIndx]
            player().y = Process.placeHoldersY[placeHolderIndx]
            animateRock.visibility = View.GONE
            player().visibility = View.VISIBLE

            var noJump = true
            if (Process.playerAndIndex[0] == 1) {
                val oldLocation = Process.player1Index[Process.playerAndIndex[1]]
                Process.player1Index[Process.playerAndIndex[1]] = placeHolderIndx
                if (Process.jumpList.isNotEmpty()) {
                    for (j in Process.jumpList) {
                        if (j[0] == placeHolderIndx && j[2] == Process.playerAndIndex[1]) {
                            val lastLocation = Process.player2Index[j[1]]
                            Process.player2Index[j[1]] = -1
                            player2List[j[1]].visibility = View.INVISIBLE
                            Process.lastMove.add(Process.playerAndIndex[1])//Index of player 1
                            Process.lastMove.add(oldLocation)//Of player 1
                            Process.lastMove.add(j[1])//index of player 2 (Hidden)
                            Process.lastMove.add(lastLocation)//Of player 2
                            noJump = false
                            break
                        }
                    }
                }
                if (noJump) {
                    Process.lastMove.add(Process.playerAndIndex[1])//Index of player 1
                    Process.lastMove.add(oldLocation)
                }
            }else{
                val oldLocation = Process.player2Index[Process.playerAndIndex[1]]
                Process.player2Index[Process.playerAndIndex[1]] = placeHolderIndx
                if (Process.jumpList.isNotEmpty()) {
                    for (j in Process.jumpList) {
                        if (j[0] == placeHolderIndx && j[2] == Process.playerAndIndex[1]) {
                            val lastLocation = Process.player1Index[j[1]]
                            Process.player1Index[j[1]] = -1
                            player1List[j[1]].visibility = View.INVISIBLE
                            Process.lastMove.add(Process.playerAndIndex[1])//Index of player 2
                            Process.lastMove.add(oldLocation)//Of player 2
                            Process.lastMove.add(j[1])// index of player 1 (Hidden)
                            Process.lastMove.add(lastLocation)//Of player 1
                            noJump = false
                            if (Process.playersNumber == 1) {
                                Process.lastMove.clear()
                                val cpu = Process.cpuChoice(Process.playerAndIndex[1])
                                if (cpu != -1) {
                                    moveRock(cpu)
                                    break
                                }
                                Process.playerTurn = 1
                                player2.setColorFilter(R.color.black)
                                player1.clearColorFilter()
                            }
                            break
                        }
                    }
                }
                if (noJump && Process.playersNumber == 1) {
                    Process.lastMove.clear()
                    Process.playerTurn = 1
                    player2.setColorFilter(R.color.black)
                    player1.clearColorFilter()
                }
                else if (noJump && Process.playersNumber == 2) {
                    Process.lastMove.add(Process.playerAndIndex[1])//Index of player 2
                    Process.lastMove.add(oldLocation)
                }
            }
            if (Process.playerAndIndex[0] == 1) {
                endTurn1.clearColorFilter()
                undo1.clearColorFilter()
            }
            else if (Process.playersNumber == 2) {
                endTurn2.clearColorFilter()
                undo2.clearColorFilter()
            }
            if (Process.player1Index.max() == -1) gameOver(getText(R.string.game_over2).toString())
            if (Process.player2Index.max() == -1) gameOver(getText(R.string.game_over1).toString())
            enableAllLabels(true)
        }, (distance*7/Process.cDensity).toLong())
    }

    private fun player(): ImageView {

        return if (Process.playerAndIndex[0] == 1) player1List[Process.playerAndIndex[1]]
               else player2List[Process.playerAndIndex[1]]
    }

    private fun enableAllLabels(bool: Boolean) {

        for (pH in placeHolders) pH.isEnabled = bool
        for (pR1 in player1List) pR1.isEnabled = bool
        for (pR2 in player2List) pR2.isEnabled = bool
        endTurn1.isEnabled = bool
        endTurn2.isEnabled = bool
        undo1.isEnabled = bool
        undo2.isEnabled = bool
    }

    private fun showCount() {

        msg.text = Process.countDown.toString()
        popup.showAtLocation(board, Gravity.CENTER, 0, 0)
        Handler(Looper.getMainLooper()).postDelayed({
            popup.dismiss()
            if (Process.countDown == 0) gameOver(getText(R.string.game_over0).toString())
        }, 1000)
    }

    private fun gameOver(result: String) {

        val dialogG = AlertDialog.Builder(this)
        dialogG.setCancelable(false)
        dialogG.setTitle(getText(R.string.game_over).toString())
        dialogG.setMessage(result)
        dialogG.setPositiveButton(getText(R.string.restart).toString(), this)//-1
        dialogG.setNegativeButton(getText(R.string.back_to_menu).toString(), this)//-2
        dialogG.show()
        //getText(R.string.s_one)
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        Process.reset()
        startActivity(Intent(applicationContext, FullscreenActivity::class.java))
        finish()
    }

    override fun onClick(dialog: DialogInterface?, which: Int) {

        if (which == -1) {
            Process.reset()
            recreate()
        }
        else {
            Process.reset()
            startActivity(Intent(applicationContext, FullscreenActivity::class.java))
            finish()
        }
    }
}










