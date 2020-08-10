package com.sbizzera.real_estate_manager.ui.rem_activity

import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sbizzera.real_estate_manager.data.CurrentPropertyIdRepository
import com.sbizzera.real_estate_manager.ui.rem_activity.details_property_fragment.DetailsPropertyFragment
import com.sbizzera.real_estate_manager.utils.SharedPreferencesRepo
import com.sbizzera.real_estate_manager.utils.SingleLiveEvent
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch
import java.util.*

class REMActivityViewModel(
    private val synchroniseDataHelper: SynchroniseDataHelper,
    private val sharedPreferencesRepo: SharedPreferencesRepo,
    private val currentPropertyIdRepository: CurrentPropertyIdRepository
) : ViewModel() {

    val viewAction = SingleLiveEvent<ViewAction>()


    fun onPhotoEditorAsked() {
        viewAction.value = ViewAction.LaunchPhotoEditor
    }

    fun onPropertyDetailsAsked() {
        viewAction.value = ViewAction.LaunchDetails
    }

    fun onAddOrModifyPropertyAsked() {
        viewAction.value = ViewAction.LaunchEditProperty
    }

    fun onMapAsked() {
        viewAction.value = ViewAction.LaunchMap
    }

    fun syncLocalAndRemoteData() {
        viewModelScope.launch(IO) {
            synchroniseDataHelper.synchroniseData()
        }
    }

    fun onPhotoViewerAsked(transitionView: View) {
        viewAction.value = ViewAction.LaunchPhotoViewer(transitionView)
    }

    fun checkUserIsLogged() {
        val userName = sharedPreferencesRepo.getUserName()
        if (userName == null) {
            viewAction.value = ViewAction.ShowChooseUserDialog
        }
    }

    fun setUserNameInSharedPrefs(userName: String) {
        if (userName.isNotEmpty()) {
            val userNameNormalised = userName.apply {
                trim { it == ' ' }
                toUpperCase(Locale.getDefault())
            }

            sharedPreferencesRepo.insertUserName(userNameNormalised)
        }
    }

    fun logOut() {
        sharedPreferencesRepo.insertUserName(null)
    }

    private fun clearCurrentPropertyId() {
        currentPropertyIdRepository.currentPropertyIdLiveData.value = null
    }

    fun shouldDisplayBackIconAndClearCurrentPropertyRepo(backStackList: MutableList<String>) {
        if(!backStackList.contains(DetailsPropertyFragment::class.java.simpleName)){
            clearCurrentPropertyId()
            viewAction.value = ViewAction.HideBackButton
        }
    }

    sealed class ViewAction {
        object LaunchPhotoEditor : ViewAction()
        object LaunchDetails : ViewAction()
        object LaunchEditProperty : ViewAction()
        object LaunchMap : ViewAction()
        class LaunchPhotoViewer(val transitionView: View) : ViewAction()
        object ShowChooseUserDialog : ViewAction()
        object HideBackButton : ViewAction()
    }

}



