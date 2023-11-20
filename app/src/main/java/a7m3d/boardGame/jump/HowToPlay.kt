package a7m3d.boardGame.jump

import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import kotlin.math.roundToInt


class HowToPlay(val parent: FullscreenActivity) : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        activity?.onBackPressedDispatcher?.addCallback(
            viewLifecycleOwner, object: OnBackPressedCallback(true)  {
                override fun handleOnBackPressed() {
                    for (b in parent.buttons) b.visibility = View.VISIBLE
                    parent.changeLan.visibility = View.VISIBLE
                    parent.logo.visibility = View.VISIBLE
                    activity?.supportFragmentManager?.beginTransaction()?.remove(
                        parent.howToPlay)?.commit()
                }
        })
        return inflater.inflate(R.layout.how_to_play, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val txtId = listOf(R.id.txt0, R.id.txt1 ,R.id.txt2 ,R.id.txt3 ,R.id.txt4)
        val imgId = listOf(R.id.img0, R.id.img1, R.id.img2, R.id.img3)
        for (t in txtId) {
            val txt = view.findViewById<TextView>(t)
            //Process.chngImgSize(txt, 1, 4)
            val param = txt.layoutParams
            param.width = (Process.cWidth * Process.cDensity).roundToInt()
            txt.layoutParams = param
            txt.textSize = Process.cWidth/10.5f
            //txt.typeface = Typeface.defaultFromStyle(Typeface.ITALIC)
        }
        for (i in imgId) {
            val img = view.findViewById<ImageView>(i)
            Process.chngImgSize(img, 1, 4)
        }
    }
}