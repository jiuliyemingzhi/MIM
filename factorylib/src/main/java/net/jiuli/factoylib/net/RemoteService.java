package net.jiuli.factoylib.net;

import net.jiuli.factoylib.model.api.RespModel;
import net.jiuli.factoylib.model.api.account.AccountRespModel;
import net.jiuli.factoylib.model.api.account.LoginModel;
import net.jiuli.factoylib.model.api.account.RegisterModel;
import net.jiuli.factoylib.model.api.group.GroupCreateModel;
import net.jiuli.factoylib.model.api.group.GroupMemberAddModel;
import net.jiuli.factoylib.model.api.user.UserUpdateModel;
import net.jiuli.factoylib.model.card.GroupCard;
import net.jiuli.factoylib.model.card.GroupMemberCard;
import net.jiuli.factoylib.model.card.MessageCard;
import net.jiuli.factoylib.model.card.UserCard;
import net.jiuli.factoylib.model.message.MessageCreateModel;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

/**
 * ..Created by jiuli on 17-9-14.
 */

public interface RemoteService {

    @POST("account/register")
    Call<RespModel<AccountRespModel>> accountRegister(@Body RegisterModel model);

    @POST("account/login")
    Call<RespModel<AccountRespModel>> accountLogin(@Body LoginModel model);

    @POST("account/bind/{pushId}")
    Call<RespModel<AccountRespModel>> accountBind(@Path(encoded = true, value = "pushId") String pushId);

    @PUT("user")
    Call<RespModel<UserCard>> userUpdate(@Body UserUpdateModel model);

    @PUT("user/follow/{followId}")
    Call<RespModel<UserCard>> follow(@Path(encoded = true, value = "followId") String followId);

    @GET("user/search/{name}")
    Call<RespModel<List<UserCard>>> search(@Path(encoded = true, value = "name") String name);

    @GET("user/followers")
    Call<RespModel<List<UserCard>>> followers();

    @GET("user/user/{id}")
    Call<RespModel<UserCard>> userFind(@Path(encoded = true, value = "id") String id);

    @GET("user/contact")
    Call<RespModel<List<UserCard>>> contacts();

    @POST("msg")
    Call<RespModel<MessageCard>> pushMessage(@Body MessageCreateModel model);

    @GET("group/find/{groupId}")
    Call<RespModel<GroupCard>> groupFind(@Path(encoded = true, value = "groupId") String groupId);

    @POST("group/create")
    Call<RespModel<GroupCard>> groupCreate(@Body GroupCreateModel model);

    @GET("group/search/{name}")
    Call<RespModel<List<GroupCard>>> groupSearch(@Path(encoded = true, value = "name") String name);

    @GET("group/list/{date}")
    Call<RespModel<List<GroupCard>>> groups(@Path(encoded = true, value = "date") String date);

    @GET("group/{groupId}/member")
    Call<RespModel<List<GroupMemberCard>>> members(@Path(encoded = true, value = "groupId") String groupId);

    @POST("group/{groupId}/member")
    Call<RespModel<List<GroupMemberCard>>> addMembers(@Path(encoded = true, value = "groupId") String groupId, @Body GroupMemberAddModel model);


}
