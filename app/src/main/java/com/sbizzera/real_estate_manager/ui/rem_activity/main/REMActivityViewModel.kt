package com.sbizzera.real_estate_manager.ui.rem_activity.main

import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sbizzera.real_estate_manager.data.repository.CurrentPropertyIdRepository
import com.sbizzera.real_estate_manager.ui.rem_activity.details_property.DetailsPropertyFragment
import com.sbizzera.real_estate_manager.data.repository.SharedPreferencesRepo
import com.sbizzera.real_estate_manager.utils.architecture_components.SingleLiveEvent
import com.sbizzera.real_estate_manager.utils.helper.SynchroniseDataHelper
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class REMActivityViewModel(
    private val synchroniseDataHelper: SynchroniseDataHelper,
    private val sharedPreferencesRepo: SharedPreferencesRepo,
    private val currentPropertyIdRepository: CurrentPropertyIdRepository
) : ViewModel() {

    val viewAction =
        SingleLiveEvent<ViewAction?>()

    init {
        checkUserIsLogged()
    }

    fun onPhotoEditorAsked() {
        viewAction.value =
            ViewAction.LaunchPhotoEditor
    }

    fun onPropertyDetailsAsked() {
        viewAction.value =
            ViewAction.LaunchDetails
    }

    fun onAddOrModifyPropertyAsked() {
        viewAction.value =
            ViewAction.LaunchEditProperty
    }

    fun onMapAsked() {
        viewAction.value =
            ViewAction.LaunchMap
    }

    fun syncLocalAndRemoteData() {
        viewAction.value =
            ViewAction.LaunchSync
        viewModelScope.launch(IO) {
            synchroniseDataHelper.synchroniseData()
            withContext(Main) {
                viewAction.value =
                    ViewAction.SyncEnd
            }
        }
    }

    fun onPhotoViewerAsked(transitionView: View) {
        viewAction.value =
            ViewAction.LaunchPhotoViewer(
                transitionView
            )
    }

    private fun checkUserIsLogged() {
        val userName = sharedPreferencesRepo.getUserName()
        if (userName == null) {
            viewAction.value =
                ViewAction.ShowChooseUserDialog
        }
    }

    fun setUserNameInSharedPrefs(userName: String) {
        if (userName.isNotEmpty()) {
            val userNameNormalised = userName.apply {
                trim { it == ' ' }
                toUpperCase(Locale.getDefault())
            }
            viewModelScope.launch(IO) {
               sharedPreferencesRepo.insertUserName(userNameNormalised)
                withContext(Main) {
                    checkUserIsLogged()
                }
            }
        }else{
            checkUserIsLogged()
        }
    }

    fun logOut() {
        sharedPreferencesRepo.insertUserName(null)
        checkUserIsLogged()
    }

    private fun clearCurrentPropertyId() {
        currentPropertyIdRepository.currentPropertyIdLiveData.value = null
    }

    fun shouldDisplayBackIconAndClearCurrentPropertyRepo(backStackList: MutableList<String>) {
        if (!backStackList.contains(DetailsPropertyFragment::class.java.simpleName)) {
            clearCurrentPropertyId()
            viewAction.value =
                ViewAction.HideBackButton
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
        object LaunchSync : ViewAction()
        object SyncEnd : ViewAction()
    }

}



