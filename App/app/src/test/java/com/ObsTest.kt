package com

import com.community.messenger.common.util.SnapshotNotFoundException
import io.reactivex.Observable
import org.junit.Test

class ObsTest {

    @Test
    fun test(){
        val obs = Observable.create<String> {
            it.onNext("1")
            it.tryOnError(SnapshotNotFoundException("2"))
            it.onNext("3")
            it.onComplete()
        }

        val obs2 = Observable.create<String> {
            it.onNext("3")
            it.onComplete()
        }


        var cnt  = 0
        obs.onErrorResumeNext(obs2).subscribe(
            {
                cnt++
                println(it)
            },
            {}
        )

       assert(obs.blockingLast() == "3")
    }
}