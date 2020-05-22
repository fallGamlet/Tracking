package com.tracking.observer.ui.map

import androidx.lifecycle.ViewModel
import com.tracking.observer.ui.SimpleViewState
import com.tracking.observer.ui.utils.LogUtils
import com.tracking.observer.ui.utils.subscribeWithDisposable
import com.tracking.observer.ui.utils.threadsIOtoUI
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.disposables.Disposable
import java.util.concurrent.TimeUnit

class MapViewModel : ViewModel() {

    val viewState = SimpleViewState<List<GeoPoint>>()
    private var pointsDisposable: Disposable? = null

    init {
        pointsDisposable = Observable.interval(3, TimeUnit.SECONDS)
                .doOnNext {
                    LogUtils.log("Timer", "emmit signal $it")
                }
                .flatMap { getGeoPoints().toObservable() }
                .threadsIOtoUI()
                .doOnSubscribe { viewState.loading.value = true }
                .doOnTerminate { viewState.loading.value = false }
                .doOnNext { viewState.data.value = it }
                .doOnError { viewState.error.value = it }
                .subscribeWithDisposable()
    }

    private fun getGeoPoints() = Single.fromPublisher<List<GeoPoint>> {
        // TODO replace to get real data
        val items = createFakePoints()
        it.onNext(items)
    }

    private fun createFakePoints(): List<GeoPoint> {
        return listOf(
                GeoPoint(
                        id = "1",
                        title = "19",
                        type = "trolley_bus",
                        latitude = 46.843597,
                        longitude = 29.627806
                ),
                GeoPoint(
                        id = "2",
                        title = "2",
                        type = "minibus",
                        latitude = 46.827697,
                        longitude = 29.631506
                ),
                GeoPoint(
                        id = "3",
                        title = "2",
                        type = "trolley_bus",
                        latitude = 46.830000,
                        longitude = 29.647200
                )
        )
    }

    override fun onCleared() {
        super.onCleared()
        pointsDisposable?.dispose()
        pointsDisposable = null
    }

}