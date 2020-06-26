package com.sbizzera.real_estate_manager.utils

import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.view.animation.RotateAnimation
import com.sbizzera.real_estate_manager.App

object AnimationHelper {

    val appContext = App.getInstance()

    fun appearInAnimation(): AlphaAnimation {
        val animation = AlphaAnimation(0f, 1f)
        animation.duration = 300
        animation.fillAfter = true
        animation.isFillEnabled = true
        return animation
    }

    fun disappearAnimation(): AlphaAnimation {
        val animation = AlphaAnimation(1f, 0f)
        animation.duration = 300
        animation.fillAfter = true
        animation.isFillEnabled = true
        return animation
    }

    private fun rotationOnSelf45DegreesClock(): Animation {
        val rotateAnimation = RotateAnimation(0f, 45f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
        rotateAnimation.interpolator = LinearInterpolator()
        rotateAnimation.fillAfter = true
        rotateAnimation.isFillEnabled = true
        rotateAnimation.duration = 300
        return rotateAnimation
    }

    private fun rotationOnSelf45DegreesCounterClock(): Animation {
        val rotateAnimation = RotateAnimation(45f, 0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f)
        rotateAnimation.interpolator = LinearInterpolator()
        rotateAnimation.fillAfter = true
        rotateAnimation.duration = 300
        rotateAnimation.isFillEnabled = true
        return rotateAnimation
    }

    fun getAddPhotoFabAnimation(menuIsOpen: Boolean): Animation {
        return if (menuIsOpen) rotationOnSelf45DegreesCounterClock() else rotationOnSelf45DegreesClock()
    }

    fun getAddPhotoFromGalleryFabAnimation(menuIsOpen: Boolean): Animation {
        return if (menuIsOpen) disappearAnimation() else appearInAnimation()
    }

    fun getAddPhotoFromCameraFabAnimation(menuIsOpen: Boolean): Animation {
        return if (menuIsOpen) disappearAnimation() else appearInAnimation()
    }

}
