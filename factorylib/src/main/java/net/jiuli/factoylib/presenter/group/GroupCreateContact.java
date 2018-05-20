package net.jiuli.factoylib.presenter.group;

import net.jiuli.common.factory.presenter.BaseContract;
import net.jiuli.factoylib.model.Author;

/**
 * Created by jiuli on 17-12-15.
 */

public interface GroupCreateContact {
    interface Presenter extends BaseContract.Presenter {
        //创建
        void create(String name, String desc, String picture);

        void changeSelect(ViewModel model, boolean isSelect);
    }

    interface View extends BaseContract.RecyclerView<Presenter, ViewModel> {
        void onCreateSucceed();
    }

    class ViewModel {
        public Author author;

        public boolean isSelected;

        public ViewModel(Author author, boolean isSelected) {
            this.author = author;
            this.isSelected = false;
        }
    }
}
