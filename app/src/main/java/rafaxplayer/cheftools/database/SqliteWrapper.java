package rafaxplayer.cheftools.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import rafaxplayer.cheftools.Globalclasses.ImageGalleryModel;
import rafaxplayer.cheftools.Globalclasses.Menu;
import rafaxplayer.cheftools.Globalclasses.Order_Product;
import rafaxplayer.cheftools.Globalclasses.Orders;
import rafaxplayer.cheftools.Globalclasses.Product;
import rafaxplayer.cheftools.Globalclasses.Recipe;
import rafaxplayer.cheftools.Globalclasses.Stock_Product;
import rafaxplayer.cheftools.Globalclasses.Stocks;
import rafaxplayer.cheftools.Globalclasses.Supplier;
import rafaxplayer.cheftools.R;


public class SqliteWrapper {
    private static String TAG = SqliteWrapper.class.getSimpleName();
    private Context con;
    private SQLiteDatabase db;
    private DBHelper dbHelper;

    public SqliteWrapper(Context context) {

        this.con = context;
        dbHelper = new DBHelper(context);
    }

    public void open() throws SQLException {
        db = dbHelper.getWritableDatabase();
    }

    public Boolean IsOpen() {
        return db.isOpen();
    }

    public void close() {
        dbHelper.close();
    }

    public Boolean freeQueryExistsorNot(String query) {

        Cursor cursor = db.rawQuery(query, null);
        if (cursor.getCount() <= 0) {
            cursor.close();
            return false;
        }
        cursor.close();
        return true;


    }

    public String getSimpleData(int id, String Column, String Table) {
        String ret = "";
        String selectQuery = "SELECT " + Column + " FROM " + Table + " WHERE " + DBHelper.ID + "=" + id;
        if (id > 0) {
            try {
                Cursor cursor = db.rawQuery(selectQuery, null);
                if (cursor != null) {
                    cursor.moveToFirst();
                    ret = cursor.getString(cursor.getColumnIndex(Column));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return ret;
    }

    public boolean CheckIsDataAlreadyInDBorNot(String TableName, String dbfield, String fieldValue) {

        String Query = "Select * from " + TableName + " where " + dbfield + " = '" + fieldValue + "'";
        try {
            Cursor cursor = db.rawQuery(Query, null);
            if (cursor.getCount() <= 0) {
                cursor.close();
                return false;
            }
            cursor.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;
    }

    public Object SelectWithId(String clase, String Table, int id) {

        String selectQuery = "SELECT * FROM " + Table + " WHERE " + DBHelper.ID + "=" + id;
        try {
            Cursor cursor = db.rawQuery(selectQuery, null);
            if (clase.equals("Recipe")) {
                Recipe rec = new Recipe();
                if (cursor != null) {
                    cursor.moveToFirst();

                    rec.setId(Integer.parseInt(cursor.getString(0)));
                    rec.setName(cursor.getString(1));
                    rec.setImg(cursor.getString(2));
                    rec.setIngredients(cursor.getString(3));
                    rec.setElaboration(cursor.getString(4));
                    rec.setUrl(cursor.getString(5));
                    rec.setCategoty(cursor.getString(7));

                }

                return rec;
            } else if (clase.equals("Menu")) {
                Menu men = new Menu();
                if (cursor != null) {
                    cursor.moveToFirst();

                    men.setId(Integer.parseInt(cursor.getString(0)));
                    men.setName(cursor.getString(1));
                    men.setEntrantes(cursor.getString(2));
                    men.setPrimeros(cursor.getString(3));
                    men.setSegundos(cursor.getString(4));
                    men.setPostre(cursor.getString(5));
                    men.setComentario(cursor.getString(6));
                    men.setFecha(String.valueOf(cursor.getString(7)));

                }
                return men;
            } else if (clase.equals("Provider")) {

                Supplier pro = new Supplier();
                if (cursor != null) {
                    cursor.moveToFirst();

                    pro.setId(Integer.parseInt(cursor.getString(0)));
                    pro.setName(cursor.getString(1));
                    pro.setTelefono(cursor.getString(2));
                    pro.setEmail(cursor.getString(3));
                    pro.setDireccion(cursor.getString(4));
                    pro.setComentario(cursor.getString(5));
                    pro.setCategoria(cursor.getString(6));
                }
                return pro;
            } else if (clase.equals("Product")) {

                Product prod = new Product();
                if (cursor != null) {
                    cursor.moveToFirst();
                    prod.setId(Integer.parseInt(cursor.getString(0)));
                    prod.setName(cursor.getString(1));
                    prod.setFormatoname(cursor.getString(2));
                    prod.setCategoryname(cursor.getString(3));
                    prod.setSuppliername(cursor.getString(4));
                    prod.setFormatoid(cursor.getInt(5));
                    prod.setCategoryid(cursor.getInt(6));
                    prod.setSupplierid(cursor.getInt(7));

                }
                return prod;
            } else if (clase.equals("Orders")) {

                Orders ord = new Orders();
                if (cursor != null) {
                    cursor.moveToFirst();
                    ord.setId(Integer.parseInt(cursor.getString(0)));
                    ord.setName(cursor.getString(1));
                    ord.setSupplierid(cursor.getInt(2));
                    ord.setComentario(cursor.getString(3));
                    ord.setFecha(cursor.getString(4));

                }
                return ord;
            } else if (clase.equals("Stocks")) {

                Stocks stock = new Stocks();
                if (cursor != null) {
                    cursor.moveToFirst();
                    stock.setId(Integer.parseInt(cursor.getString(0)));
                    stock.setName(cursor.getString(1));
                    stock.setComentario(cursor.getString(2));
                    stock.setFecha(cursor.getString(3));

                }
                return stock;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;

    }

    public int DeleteWithId(int id, String Table) {
        int count = 0;
        try {

            count = db.delete(Table, DBHelper.ID + " = ?",
                    new String[]{String.valueOf(id)});
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return count;
    }

    public int getItemsCount(String Table) {
        int count = 0;
        try {
            String countQuery = "SELECT  * FROM " + Table;
            Cursor cursor = db.rawQuery(countQuery, null);
            count = cursor.getCount();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return count;
    }

    public long UpdateWithId(Object ob, int id) {
        long count = 0;
        try {
            if (ob instanceof Recipe) {

                ContentValues values = new ContentValues();
                values.put(DBHelper.NAME, ((Recipe) ob).getName());
                values.put(DBHelper.RECETA_IMG, ((Recipe) ob).getImg());
                values.put(DBHelper.RECETA_INGREDIENTES, ((Recipe) ob).getIngredients());
                values.put(DBHelper.RECETA_ELABORACION, ((Recipe) ob).getElaboration());
                values.put(DBHelper.RECETA_CATEGORIA, ((Recipe) ob).getCategoty());
                values.put(DBHelper.RECETA_URL, ((Recipe) ob).getUrl());

                count = db.update(DBHelper.TABLE_RECETAS, values, DBHelper.ID + " = ?",
                        new String[]{String.valueOf(id)});

            } else if (ob instanceof Menu) {
                ContentValues values = new ContentValues();
                values.put(DBHelper.NAME, ((Menu) ob).getName());
                values.put(DBHelper.MENUS_CARTAS_ENTRANTES, ((Menu) ob).getEntrantes());
                values.put(DBHelper.MENUS_CARTAS_PRIMEROS, ((Menu) ob).getPrimeros());
                values.put(DBHelper.MENUS_CARTAS_SEGUNDOS, ((Menu) ob).getSegundos());
                values.put(DBHelper.MENUS_CARTAS_POSTRES, ((Menu) ob).getPostre());
                values.put(DBHelper.COMENTARIO, ((Menu) ob).getComentario());

                count = db.update(DBHelper.TABLE_MENUSCARTAS, values, DBHelper.ID + " = ?",
                        new String[]{String.valueOf(id)});
            } else if (ob instanceof Supplier) {
                ContentValues values = new ContentValues();
                values.put(DBHelper.NAME, ((Supplier) ob).getName());
                values.put(DBHelper.PROVEEDOR_TELEFONO, ((Supplier) ob).getTelefono());
                values.put(DBHelper.PROVEEDOR_EMAIL, ((Supplier) ob).getEmail());
                values.put(DBHelper.PROVEEDOR_DIRECCION, ((Supplier) ob).getDireccion());
                values.put(DBHelper.PROVEEDOR_CATEGORIA, ((Supplier) ob).getCategoria());
                values.put(DBHelper.COMENTARIO, ((Supplier) ob).getComentario());

                count = db.update(DBHelper.TABLE_PROVEEDORES, values, DBHelper.ID + " = ?",
                        new String[]{String.valueOf(id)});
            } else if (ob instanceof Product) {

                ContentValues values = new ContentValues();
                values.put(DBHelper.NAME, ((Product) ob).getName());
                values.put(DBHelper.PRODUCTO_FORMATO_NAME, ((Product) ob).getFormatoname());
                values.put(DBHelper.PRODUCTO_CATEGORIA_NAME_, ((Product) ob).getCategoryname());
                values.put(DBHelper.PRODUCTO_PROVEEDOR_NAME, ((Product) ob).getSuppliername());
                values.put(DBHelper.PRODUCTO_CATEGORIA_ID, ((Product) ob).getCategoryid());
                values.put(DBHelper.PRODUCTO_FORMATO_ID, ((Product) ob).getFormatoid());
                values.put(DBHelper.PRODUCTO_PROVEEDOR_ID, ((Product) ob).getSupplierid());

                count = db.update(DBHelper.TABLE_PRODUCTOS, values, DBHelper.ID + " = ?",
                        new String[]{String.valueOf(id)});
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return count;
    }

    public List<Object> getAllObjects(String clase, String ORDER) {
        if (ORDER == null) {
            ORDER = DBHelper.FECHA;
        }
        List<Object> ObjectList = new ArrayList<Object>();
        try {
            if (clase.equals("Menu")) {
                String selectQuery = "SELECT * FROM " + DBHelper.TABLE_MENUSCARTAS + " ORDER BY " + ORDER + " DESC";

                Cursor cursor = db.rawQuery(selectQuery, null);

                if (cursor.moveToFirst()) {
                    do {
                        Menu men = new Menu();
                        men.setId(Integer.parseInt(cursor.getString(0)));
                        men.setName(cursor.getString(1));
                        men.setEntrantes(cursor.getString(2));
                        men.setPrimeros(cursor.getString(3));
                        men.setSegundos(cursor.getString(4));
                        men.setPostre(cursor.getString(5));
                        men.setComentario(cursor.getString(6));
                        men.setFecha(String.valueOf(cursor.getString(7)));

                        ObjectList.add(men);
                    } while (cursor.moveToNext());
                }

            } else if (clase.equals("Recipe")) {
                String selectQuery = "SELECT * FROM " + DBHelper.TABLE_RECETAS + " ORDER BY " + ORDER + " DESC";

                Cursor cursor = db.rawQuery(selectQuery, null);

                if (cursor.moveToFirst()) {
                    do {
                        Recipe rec = new Recipe();
                        rec.setId(Integer.parseInt(cursor.getString(0)));
                        rec.setName(cursor.getString(1));
                        rec.setImg(cursor.getString(2));
                        rec.setIngredients(cursor.getString(3));
                        rec.setElaboration(cursor.getString(4));
                        rec.setUrl(cursor.getString(5));
                        rec.setCategoty(cursor.getString(7));

                        ObjectList.add(rec);
                    } while (cursor.moveToNext());
                }

            } else if (clase.equals("Provider")) {
                String selectQuery = "SELECT * FROM " + DBHelper.TABLE_PROVEEDORES + " ORDER BY " + DBHelper.NAME + " DESC";

                Cursor cursor = db.rawQuery(selectQuery, null);

                if (cursor.moveToFirst()) {
                    do {
                        Supplier pro = new Supplier();
                        pro.setId(Integer.parseInt(cursor.getString(0)));
                        pro.setName(cursor.getString(1));
                        pro.setTelefono(cursor.getString(2));
                        pro.setEmail(cursor.getString(3));
                        pro.setDireccion(cursor.getString(4));
                        pro.setComentario(cursor.getString(5));
                        pro.setCategoria(cursor.getString(6));

                        ObjectList.add(pro);
                    } while (cursor.moveToNext());
                }

            } else if (clase.equals("Product")) {
                String selectQuery = "SELECT * FROM " + DBHelper.TABLE_PRODUCTOS + " ORDER BY " + DBHelper.NAME + " DESC";

                Cursor cursor = db.rawQuery(selectQuery, null);

                if (cursor.moveToFirst()) {
                    do {
                        Product pro = new Product();
                        pro.setId(Integer.parseInt(cursor.getString(0)));
                        pro.setName(cursor.getString(1));
                        pro.setFormatoname(cursor.getString(2));
                        pro.setCategoryname(cursor.getString(3));
                        pro.setSuppliername(cursor.getString(4));
                        pro.setFormatoid(cursor.getInt(5));
                        pro.setCategoryid(cursor.getInt(6));
                        pro.setSupplierid(cursor.getInt(7));

                        ObjectList.add(pro);
                    } while (cursor.moveToNext());
                }

            } else if (clase.equals("Orders")) {
                String selectQuery = "SELECT * FROM " + DBHelper.TABLE_PEDIDOS + " ORDER BY " + DBHelper.NAME + " DESC";

                Cursor cursor = db.rawQuery(selectQuery, null);

                if (cursor.moveToFirst()) {
                    do {
                        Orders ord = new Orders();
                        ord.setId(Integer.parseInt(cursor.getString(0)));
                        ord.setName(cursor.getString(1));
                        ord.setSupplierid(cursor.getInt(2));
                        ord.setComentario(cursor.getString(3));
                        ord.setFecha(cursor.getString(4));

                        ObjectList.add(ord);
                    } while (cursor.moveToNext());
                }

            } else if (clase.equals("Stocks")) {
                String selectQuery = "SELECT * FROM " + DBHelper.TABLE_INVENTARIOS + " ORDER BY " + DBHelper.NAME + " DESC";

                Cursor cursor = db.rawQuery(selectQuery, null);

                if (cursor.moveToFirst()) {
                    do {
                        Stocks stock = new Stocks();
                        stock.setId(Integer.parseInt(cursor.getString(0)));
                        stock.setName(cursor.getString(1));
                        stock.setComentario(cursor.getString(2));
                        stock.setFecha(cursor.getString(3));

                        ObjectList.add(stock);
                    } while (cursor.moveToNext());
                }

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ObjectList;
    }


    public long InsertObject(Object ob) {
        long id = -1;
        ContentValues values = new ContentValues();
        try {

            if (ob instanceof Menu) {

                values.put(DBHelper.NAME, ((Menu) ob).getName());
                values.put(DBHelper.MENUS_CARTAS_ENTRANTES, ((Menu) ob).getEntrantes());
                values.put(DBHelper.MENUS_CARTAS_PRIMEROS, ((Menu) ob).getPrimeros());
                values.put(DBHelper.MENUS_CARTAS_SEGUNDOS, ((Menu) ob).getSegundos());
                values.put(DBHelper.MENUS_CARTAS_POSTRES, ((Menu) ob).getPostre());
                values.put(DBHelper.COMENTARIO, ((Menu) ob).getComentario());

                id = db.insert(DBHelper.TABLE_MENUSCARTAS, null, values);


            } else if (ob instanceof Recipe) {
                values.put(DBHelper.NAME, ((Recipe) ob).getName());
                values.put(DBHelper.RECETA_IMG, ((Recipe) ob).getImg());
                values.put(DBHelper.RECETA_INGREDIENTES, ((Recipe) ob).getIngredients());
                values.put(DBHelper.RECETA_ELABORACION, ((Recipe) ob).getElaboration());
                values.put(DBHelper.RECETA_CATEGORIA, ((Recipe) ob).getCategoty());
                values.put(DBHelper.RECETA_URL, ((Recipe) ob).getUrl());

                id = db.insert(DBHelper.TABLE_RECETAS, null, values);

            } else if (ob instanceof Supplier) {

                values.put(DBHelper.NAME, ((Supplier) ob).getName());
                values.put(DBHelper.PROVEEDOR_TELEFONO, ((Supplier) ob).getTelefono());
                values.put(DBHelper.PROVEEDOR_EMAIL, ((Supplier) ob).getEmail());
                values.put(DBHelper.PROVEEDOR_DIRECCION, ((Supplier) ob).getDireccion());
                values.put(DBHelper.PROVEEDOR_CATEGORIA, ((Supplier) ob).getCategoria());
                values.put(DBHelper.COMENTARIO, ((Supplier) ob).getComentario());

                id = db.insert(DBHelper.TABLE_PROVEEDORES, null, values);

            }
            values.clear();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (db.isOpen()) {
            db.close();
        }

        return id;
    }

    public long InsertSimpleData(String Table, String value) {
        long id = -1;
        ContentValues values = new ContentValues();
        try {
            values.put(DBHelper.NAME, value);
            id = db.insert(Table, null, values);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return id;

    }

    public long UpdateSimpleData(String Table, String value, long id) {
        long count = 0;
        ContentValues values = new ContentValues();
        try {
            values.put(DBHelper.NAME, value);
            count = db.update(Table, values, DBHelper.ID + " = ?",
                    new String[]{String.valueOf(id)});
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return count;

    }

    public ArrayList<HashMap<String, Object>> getFormatsOrCategorysData(String Table) {
        String selectQuery = "SELECT * FROM " + Table;
        ArrayList<HashMap<String, Object>> lst = new ArrayList<HashMap<String, Object>>();

        try {
            Cursor cats = db.rawQuery(selectQuery, null);
            HashMap<String, Object> m = new HashMap<String, Object>();
            m.put("ID", 0);
            m.put("Name", con.getString(R.string.none));
            lst.add(m);
            if (cats.moveToFirst()) {
                do {
                    HashMap<String, Object> mp = new HashMap<String, Object>();
                    mp.put("ID", cats.getInt(0));
                    mp.put("Name", cats.getString(1));

                    lst.add(mp);

                } while (cats.moveToNext());
            }
            cats.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lst;
    }
    //<--FIN Generales--->//

    //<-- recipes Images gallery--->//
    public List<ImageGalleryModel> getImages() {
        List<ImageGalleryModel> list = new ArrayList<ImageGalleryModel>();
        String sQuery = "SELECT _id,name,img FROM tbl_Recetas";
        Cursor cursor = db.rawQuery(sQuery, null);
        if (cursor.moveToFirst()) {
            do {
                ImageGalleryModel imgModel = new ImageGalleryModel();
                imgModel.ID = Integer.parseInt(cursor.getString(0));
                imgModel.Title = cursor.getString(1);
                imgModel.ImagePath = cursor.getString(2);
                list.add(imgModel);

            } while (cursor.moveToNext());
        }
        return list;
    }

    //<-----Stocks  List----->
    public long newStocksListName(String name, String comment) {
        long id = -1;
        ContentValues values = new ContentValues();
        values.put(DBHelper.NAME, name);

        values.put(DBHelper.COMENTARIO, comment);
        try {
            id = db.insert(DBHelper.TABLE_INVENTARIOS, null, values);
            if (db.isOpen()) {
                db.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return id;
    }

    //<-----Orders List----->
    public long newOrdersListName(String name, int prov, String comment) {
        long id = -1;
        ContentValues values = new ContentValues();
        values.put(DBHelper.NAME, name);
        values.put(DBHelper.PROVEEDOR_ID, prov);
        values.put(DBHelper.COMENTARIO, comment);
        try {
            id = db.insert(DBHelper.TABLE_PEDIDOS, null, values);
            if (db.isOpen()) {
                db.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return id;
    }

    //<-----Products List----->
    public long addProductListOrder(int listid, int prod, int cantidad, int cat, int format) {

        long id = -1;
        ContentValues values = new ContentValues();
        values.put(DBHelper.PEDIDO_ID, listid);
        values.put(DBHelper.PRODUCTO_ID, prod);
        values.put(DBHelper.PRODUCTO_CANTIDAD, cantidad);
        values.put(DBHelper.PRODUCTO_CATEGORIA_ID, cat);
        values.put(DBHelper.PRODUCTO_FORMATO_ID, format);

        try {

            id = db.insert(DBHelper.TABLE_PEDIDOS_LISTAS, null, values);
            if (db.isOpen()) {
                db.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return id;
    }

    public long addProductListStock(int stockid, int prod, int cantidad, int cat, int format) {

        long id = -1;
        ContentValues values = new ContentValues();
        values.put(DBHelper.INVENTARIO_ID, stockid);
        values.put(DBHelper.PRODUCTO_ID, prod);
        values.put(DBHelper.PRODUCTO_CANTIDAD, cantidad);
        values.put(DBHelper.PRODUCTO_CATEGORIA_ID, cat);
        values.put(DBHelper.PRODUCTO_FORMATO_ID, format);

        try {

            id = db.insert(DBHelper.TABLE_INVENTARIOS_LISTAS, null, values);
            if (db.isOpen()) {
                db.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return id;
    }


    public long addProduct(String name, int cat, int format, int prov) {
        long id = -1;
        ContentValues values = new ContentValues();
        String nameCat = cat > 0 ? this.getSimpleData(cat, DBHelper.NAME, DBHelper.TABLE_PRODUCTOS_CATEGORY) : "";
        String nameFor = format > 0 ? this.getSimpleData(format, DBHelper.NAME, DBHelper.TABLE_PRODUCTOS_FORMATO) : "";
        String nameSup = prov > 0 ? this.getSimpleData(prov, DBHelper.NAME, DBHelper.TABLE_PROVEEDORES) : "";

        values.put(DBHelper.NAME, name);
        values.put(DBHelper.PRODUCTO_FORMATO_NAME, nameFor);
        values.put(DBHelper.PRODUCTO_CATEGORIA_NAME_, nameCat);
        values.put(DBHelper.PRODUCTO_PROVEEDOR_NAME, nameSup);
        values.put(DBHelper.PRODUCTO_CATEGORIA_ID, cat);
        values.put(DBHelper.PRODUCTO_FORMATO_ID, format);
        values.put(DBHelper.PRODUCTO_PROVEEDOR_ID, prov);

        try {
            id = db.insert(DBHelper.TABLE_PRODUCTOS, null, values);
            if (db.isOpen()) {
                db.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return id;
    }

    public ArrayList<Order_Product> getProductListOrder(int listID) {

        ArrayList<Order_Product> ObjectList = new ArrayList<Order_Product>();

        String selectQuery = "SELECT * FROM " + DBHelper.TABLE_PEDIDOS_LISTAS + " WHERE " + DBHelper.PEDIDO_ID + "=" + listID;

        try {
            Cursor cursor = db.rawQuery(selectQuery, null);
            if (cursor.moveToFirst()) {
                do {
                    Order_Product ordP = new Order_Product();
                    ordP.setID(cursor.getInt(0));
                    ordP.setListaId(cursor.getInt(1));
                    ordP.setProductoId(cursor.getInt(2));
                    ordP.setCantidad(cursor.getInt(3));
                    ordP.setCategoriaid(cursor.getInt(4));
                    ordP.setFormatoid(cursor.getInt(5));

                    ObjectList.add(ordP);
                } while (cursor.moveToNext());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ObjectList;
    }

    public ArrayList<Stock_Product> getProductListStock(int listID) {

        ArrayList<Stock_Product> ObjectList = new ArrayList<Stock_Product>();

        String selectQuery = "SELECT * FROM " + DBHelper.TABLE_INVENTARIOS_LISTAS + " WHERE " + DBHelper.INVENTARIO_ID + "=" + listID;

        try {
            Cursor cursor = db.rawQuery(selectQuery, null);

            if (cursor.moveToFirst()) {
                do {
                    Stock_Product stock = new Stock_Product();
                    stock.setID(cursor.getInt(0));
                    stock.setStockId(cursor.getInt(1));
                    stock.setProductoId(cursor.getInt(2));
                    stock.setCantidad(cursor.getInt(3));
                    stock.setCategoriaid(cursor.getInt(4));
                    stock.setFormatoid(cursor.getInt(5));

                    ObjectList.add(stock);
                } while (cursor.moveToNext());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ObjectList;
    }

    public String getProductListToString(int listID, String table, String column) {
        StringBuilder str = new StringBuilder();
        if (listID != 0) {
            Log.e("ID", String.valueOf(listID));
            try {
                String selectQuery = "SELECT * FROM " + table + " WHERE " + column + "=" + listID;
                Cursor cursor = db.rawQuery(selectQuery, null);
                if (cursor.moveToFirst()) {
                    do {
                        String name = getSimpleData(cursor.getInt(2), DBHelper.NAME, DBHelper.TABLE_PRODUCTOS);
                        int cat = cursor.getInt(3);
                        String format = getSimpleData(cursor.getInt(2), DBHelper.PRODUCTO_FORMATO_NAME, DBHelper.TABLE_PRODUCTOS);
                        str.append(name);
                        str.append(" ");
                        str.append(String.valueOf(cat));
                        str.append(" ");
                        str.append(format);
                        str.append("\n");
                    } while (cursor.moveToNext());
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return str.toString();
    }

}
