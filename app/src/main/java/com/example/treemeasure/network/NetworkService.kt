package com.example.treemeasure.network

import com.example.treemeasure.Dao.TreeHeight
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface NetworkService {

//    @GET("freshman/login/login")
//    fun getLogin(@Query("name") name: String, @Query("IdCode") IdCode: String): Call<LoginResponse>
//
//    @GET("/freshman/major/comfirm")
//    fun getPersonInformation(@Query("id") id: String): Call<ComfirmResponse>
//
//    @POST("/freshman/infoCllect/submit")
//    fun postPersonInformation(@Body data: PersonInformationData): Call<ResponseBody>
//
//
//    @GET("/freshman/major/comfirm")
//    fun getConfirm(@Query("id") id: String): Call<MajorConfirmResponse>
//
//    @GET("/freshman/major/getMajor")
//    fun getMajorList(): Call<MajorResponse>
//
//    @POST("/freshman/changeMajor/changeMajor")
//    fun postChangeMajor(@Body data: MajorData): Call<ResponseBody>
//
//
//    @POST("/freshman/domitorySure/changeDormitory")
//    fun postDormitory(@Body data: DormitoryData): Call<ResponseBody>
//
//
//    @GET("/freshman/chooseClothes/getClothesList")
//    fun getClothesList(): Call<ClothesResponse>
//
//    @POST("/freshman/chooseClothes/addClothes")
//    fun postClothes(@Body data: ClothesData): Call<ResponseBody>
//
//    @GET("/freshman/arrivePlan/getArriveWay")
//    fun getArriveWayList(): Call<ArriveWayResponse>
//
//    @GET("/freshman/arrivePlan/getArriveTime")
//    fun getArriveTimeList(): Call<ArriveTimeResponse>
//
//    @POST("/freshman/arrivePlan/submit")
//    fun postArrivePlan(@Body data: ArrivePlanData): Call<ResponseBody>

    @POST("/freshman/arrivePlan/submit")
    fun postTreeHeight(@Body data: TreeHeight): Call<ResponseBody>
}