package net.jiuli.factoylib.data.helper;

import com.raizlabs.android.dbflow.sql.language.SQLite;

import net.jiuli.common.factory.data.DataSource;
import net.jiuli.factoylib.Factory;
import net.jiuli.factoylib.R;
import net.jiuli.factoylib.model.card.UserCard;
import net.jiuli.factoylib.model.api.RespModel;
import net.jiuli.factoylib.model.api.user.UserUpdateModel;
import net.jiuli.factoylib.model.db.User;
import net.jiuli.factoylib.model.db.view.UserSampleModel;
import net.jiuli.factoylib.model.db.User_Table;
import net.jiuli.factoylib.net.NetWork;
import net.jiuli.factoylib.net.RemoteService;
import net.jiuli.factoylib.persistence.Account;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by jiuli on 17-9-16.
 */

public class UserHelper {

    public final static Call search(String name, final DataSource.Callback<List<UserCard>> callback) {
        RemoteService service = NetWork.remote();
        Call<RespModel<List<UserCard>>> call = service.search(name);
        call.enqueue(new Callback<RespModel<List<UserCard>>>() {
            @Override
            public void onResponse(Call<RespModel<List<UserCard>>> call, Response<RespModel<List<UserCard>>> response) {
                RespModel<List<UserCard>> respModel = response.body();
                if (respModel.success()) {
                    callback.onDataLoaded(respModel.getResult());
                } else {
                    Factory.decodeRespCode(respModel, callback);
                }
            }

            @Override
            public void onFailure(Call<RespModel<List<UserCard>>> call, Throwable t) {
                callback.onDataNotAvailable(R.string.data_network_error);
            }
        });
        return call;
    }

    public final static void update(UserUpdateModel model, final DataSource.Callback<UserCard> callback) {
        RemoteService service = NetWork.remote();
        Call<RespModel<UserCard>> call = service.userUpdate(model);
        call.enqueue(new Callback<RespModel<UserCard>>() {
            @Override
            public void onResponse(Call<RespModel<UserCard>> call, Response<RespModel<UserCard>> response) {
                RespModel<UserCard> userCardRespModel = response.body();
                if (userCardRespModel.success()) {
                    UserCard userCard = userCardRespModel.getResult();
                    Factory.getUserCenter().dispatch(userCard);
                    callback.onDataLoaded(userCard);
                } else {
                    Factory.decodeRespCode(userCardRespModel, callback);
                }
            }

            @Override
            public void onFailure(Call<RespModel<UserCard>> call, Throwable t) {
                callback.onDataNotAvailable(R.string.data_network_error);
            }
        });
    }

    public final static void follow(String followId, final DataSource.Callback<UserCard> callback) {
        RemoteService service = NetWork.remote();
        Call<RespModel<UserCard>> call = service.follow(followId);
        call.enqueue(new Callback<RespModel<UserCard>>() {
            @Override
            public void onResponse(Call<RespModel<UserCard>> call, Response<RespModel<UserCard>> response) {
                RespModel<UserCard> respModel = response.body();
                if (respModel.success()) {
                    UserCard userCard = respModel.getResult();
                    Factory.getUserCenter().dispatch(userCard);
                    callback.onDataLoaded(userCard);
                } else {
                    Factory.decodeRespCode(respModel, callback);
                }
            }

            @Override
            public void onFailure(Call<RespModel<UserCard>> call, Throwable t) {
                callback.onDataNotAvailable(R.string.data_network_error);
            }
        });
    }


    public final static void contacts(final DataSource.Callback<List<UserCard>> callback) {
        RemoteService service = NetWork.remote();
        Call<RespModel<List<UserCard>>> call = service.contacts();
        call.enqueue(new Callback<RespModel<List<UserCard>>>() {
            @Override
            public void onResponse(Call<RespModel<List<UserCard>>> call, Response<RespModel<List<UserCard>>> response) {
                RespModel<List<UserCard>> respModel = response.body();
                if (respModel.success()) {
                    callback.onDataLoaded(respModel.getResult());
                } else {
                    Factory.decodeRespCode(respModel, callback);
                }
            }

            @Override
            public void onFailure(Call<RespModel<List<UserCard>>> call, Throwable t) {
                callback.onDataNotAvailable(R.string.data_network_error);
            }
        });
    }

    public final static User getUser(String id) {
        User user = findForLocal(id);
        if (user == null) {
            return findForNet(id);
        }
        return user;
    }

    public final static User findForLocal(String id) {
        return SQLite.select().from(User.class).where(User_Table.id.eq(id)).querySingle();
    }

    public final static User findForNet(String id) {
        RemoteService service = NetWork.remote();
        try {
            Response<RespModel<UserCard>> response = service.userFind(id).execute();
            UserCard userCard = response.body().getResult();
            if (userCard != null) {
                //数据库刷新但是没有刷新
                User user = userCard.build();
                Factory.getUserCenter().dispatch(userCard);
                return user;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public final static User searchFirstOfNet(String id) {
        User user = findForNet(id);
        if (user == null) {
            return findForLocal(id);
        }
        return user;
    }

    public final static void contacts(final DataSource.SucceedCallback<List<User>> callback) {
        RemoteService service = NetWork.remote();
        Call<RespModel<List<UserCard>>> contacts = service.contacts();
        contacts.enqueue(new Callback<RespModel<List<UserCard>>>() {
            @Override
            public void onResponse(Call<RespModel<List<UserCard>>> call, Response<RespModel<List<UserCard>>> response) {
                RespModel<List<UserCard>> respModel = response.body();
                if (respModel.success()) {
                    List<UserCard> cards = respModel.getResult();
                    List<User> userList = new ArrayList<>();
                    for (UserCard card : cards) {
                        userList.add(card.build());
                    }
                    callback.onDataLoaded(userList);
                } else {
                    Factory.decodeRespCode(respModel, null);
                }
            }

            @Override
            public void onFailure(Call<RespModel<List<UserCard>>> call, Throwable t) {

            }
        });
    }


    public final static void refreshContacts() {
        RemoteService service = NetWork.remote();
        service.contacts().enqueue(new Callback<RespModel<List<UserCard>>>() {
            @Override
            public void onResponse(Call<RespModel<List<UserCard>>> call, Response<RespModel<List<UserCard>>> response) {
                RespModel<List<UserCard>> respModel = response.body();
                if (respModel.success()) {
                    List<UserCard> cards = respModel.getResult();
                    if (cards != null && cards.size() > 0) {
                        Factory.getUserCenter().dispatch(cards.toArray(new UserCard[0]));
                    }
                } else {
                    Factory.decodeRespCode(respModel, null);
                }
            }

            @Override
            public void onFailure(Call<RespModel<List<UserCard>>> call, Throwable t) {

            }
        });
    }

    public final  static List<UserSampleModel> getSampleContact() {
        return SQLite.select(User_Table.id.withTable().as("id"),
                User_Table.name.withTable().as("name"),
                User_Table.alias.withTable().as("alias"),
                User_Table.portrait.withTable().as("portrait"))
                .from(User.class)
                .where(User_Table.isFollow.eq(true))
                .and(User_Table.id.notEq(Account.getUserId()))
                .orderBy(User_Table.name, true)
                .queryCustomList(UserSampleModel.class);
    }
}
