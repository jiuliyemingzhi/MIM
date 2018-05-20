package net.jiuli.factoylib.model.db;

import com.raizlabs.android.dbflow.structure.BaseModel;

import net.jiuli.factoylib.model.Db;
import net.jiuli.common.utils.DiffUiDataCallback;

/**
 * Created by jiuli on 17-10-8.
 */

public  abstract class BaseDbModel<Model> extends BaseModel implements DiffUiDataCallback.UiDataDiffer<Model>,Db {

}
