package com.csd.mod_achievement

import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.fragment.app.Fragment
import com.csd.lib_common.constant.ROUTE_ACHIEVEMENT_FRAGMENT_MAIN
import com.therouter.router.Route
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import androidx.appcompat.widget.Toolbar
import com.csd.lib_room.database.User
import com.csd.lib_room.manager.UserManager
import com.example.mod_achievement.R
import kotlin.math.roundToInt

/**
 * A simple [Fragment] subclass.
 * Use the [AchievementMain.newInstance] factory method to
 * create an instance of this fragment.
 */
@Route(path = ROUTE_ACHIEVEMENT_FRAGMENT_MAIN)
class AchievementMain : Fragment() {

    private var firstRow: LinearLayoutCompat? = null
    private var secondRow: LinearLayoutCompat? = null
    private var thirdRow: LinearLayoutCompat? = null
    private val user: User = UserManager.getUser()
    private val gameTimes = user.gameTimes
    private val socialTimes = user.socialTimes
    private val screenshotTimes = user.screenshotTimes
    private lateinit var fragmentContainer : FrameLayout

//    companion object {
//        fun newInstance(frameLayout: FrameLayout): AchievementMain {
//            val fragment = AchievementMain()
//            val args = Bundle()
//            args.putParcelable("myFrameLayout", frameLayout)
//            fragment.arguments = args
//            return fragment
//        }
//    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentContainer = activity?.findViewById(R.id.myFragment)!!
        Log.d("--------------------------=======", "onCreateView: -=-=-=-=-=-=-=-=-==-=-=-=-=-=-=-= ")
        return inflater.inflate(R.layout.achievement_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        firstRow = addLinearLayout()
        secondRow = addLinearLayout()
        thirdRow = addLinearLayout()

        val topBar = view.findViewById<Toolbar>(R.id.topToolbar1)

        // 找到按钮
        val myButton = topBar.findViewById<ImageButton>(R.id.back)

        // 设置按钮的点击监听器
        myButton.setOnClickListener {
            parentFragmentManager.beginTransaction().remove(this).commit()
        }


        val gameKing = ImageView(context)
        if(gameTimes<100){
            convertToBlackAndWhite(gameKing)
        }
        gameKing.setOnClickListener { gameKingFragment() }
        addAchievementToRow(firstRow!!, gameKing, R.drawable.achievement_game_king, fragmentContainer)

        //截屏达人勋章
        val screenShot = ImageView(context)
        if(screenshotTimes<100) {
            convertToBlackAndWhite(screenShot)
        }
        screenShot.setOnClickListener { screenShotFragment() }
        addAchievementToRow(firstRow!!,screenShot, R.drawable.achievement_screen_shot, fragmentContainer)

        //社交达人勋章
        val socializingKing = ImageView(context)
        if(socialTimes<100){
            convertToBlackAndWhite(socializingKing)
        }
        socializingKing.setOnClickListener { socialFragment() }
        addAchievementToRow(firstRow!!,socializingKing, R.drawable.achievement_socializing_king, fragmentContainer)
    }

    private fun addAchievementToRow(row: LinearLayoutCompat, imageView: ImageView, resId: Int, fragmentContainer: FrameLayout) {
        imageView.setImageResource(resId)

        fragmentContainer.post {
            val containerWidth = fragmentContainer.width
            val containerHeight = fragmentContainer.height

            // Set the size of the view
            val viewWidth = containerWidth / 3 // One third of container width
            val viewHeight = containerHeight / 4 // One fourth of container height

            val params = LinearLayoutCompat.LayoutParams(viewWidth, viewHeight)
            params.topMargin = dpToPx(10)
            imageView.layoutParams = params
        }




        row.addView(imageView)
    }

    private fun dpToPx(dp: Int): Int {
        val density = resources.displayMetrics.density
        return (dp * density).roundToInt()
    }

    private fun addLinearLayout(): LinearLayoutCompat {
        val myLayout: FrameLayout = requireView().findViewById(R.id.fragment_container)
        val context = requireContext()

        val newLinearLayout = LinearLayoutCompat(context)
        newLinearLayout.orientation = LinearLayoutCompat.HORIZONTAL

        val params = LinearLayoutCompat.LayoutParams(
            LinearLayoutCompat.LayoutParams.MATCH_PARENT,
            LinearLayoutCompat.LayoutParams.WRAP_CONTENT
        )
        newLinearLayout.layoutParams = params

        myLayout.addView(newLinearLayout)

        return newLinearLayout
    }

    private fun gameKingFragment() {
        val gameKingFragment = AchievementFragment.newInstance(R.layout.achievement_game_king_fragment, gameTimes)
        gameKingFragment.show(childFragmentManager, "gameKingFragment")
    }

    private fun screenShotFragment() {
        val screenShotFragment = AchievementFragment.newInstance(R.layout.achievement_screen_shot_fragment, screenshotTimes)
        screenShotFragment.show(childFragmentManager, "screenShotFragment")
    }

    private fun socialFragment() {
        val socialFragment = AchievementFragment.newInstance(R.layout.achivement_social_fragment, socialTimes)
        socialFragment.show(childFragmentManager, "socialFragment")
    }

    private fun convertToBlackAndWhite(imageView: ImageView) {
        val matrix = ColorMatrix()
        matrix.setSaturation(0f) // 设置饱和度为0，即将图片变成黑白

        val filter = ColorMatrixColorFilter(matrix)
        imageView.colorFilter = filter
    }
}