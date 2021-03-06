package rafaxplayer.cheftools.database;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Resources;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import rafaxplayer.cheftools.R;


public class DBHelper extends SQLiteOpenHelper {
    //database name
    public static final String DATABASE_NAME = "ChefToolsDB";
    //Tablas nombres...
    public static final String TABLE_RECETAS = "tbl_Recetas";
    public static final String TABLE_MENUSCARTAS = "tbl_Menus_Cartas";
    public static final String TABLE_PROVEEDORES = "tbl_Proveedores";
    public static final String TABLE_PEDIDOS = "tbl_Pedidos";
    public static final String TABLE_INVENTARIOS = "tbl_Inventarios";
    public static final String TABLE_PRODUCTOS = "tbl_Productos";
    public static final String TABLE_PRODUCTOS_FORMATO = "tbl_Productos_formato";
    public static final String TABLE_PRODUCTOS_CATEGORY = "tbl_Productos_categoria";
    public static final String TABLE_RECETAS_CATEGORIA = "tbl_Recetas_categoria";
    public static final String TABLE_PEDIDOS_LISTAS = "tbl_Pedidos_listas";
    public static final String TABLE_INVENTARIOS_LISTAS = "tbl_Inventarios_listas";
    public static final String TABLE_ESCANDALLOS = "tbl_Escandallos";
    public static final String TABLE_ESCANDALLOS_PRODUCTS = "tbl_Escandallo_Products";

    //Campos generales...
    public static final String ID = "_id";
    public static final String NAME = "name";
    public static final String FECHA = "date";
    public static final String COMENTARIO = "comentario";

    //Campos recetas tabla
    //ID y NAME generales
    public static final String RECETA_INGREDIENTES = "ingredientes";
    public static final String RECETA_ELABORACION = "elaboracion";
    public static final String RECETA_IMG = "img";
    public static final String RECETA_CATEGORIA = "categoria";
    public static final String RECETA_URL = "url";

    //Campos Menus Cartas tabla
    //ID ,FECHA y NAME generales
    public static final String MENUS_CARTAS_ENTRANTES = "mc_entrantes";
    public static final String MENUS_CARTAS_PRIMEROS = "mc_primeros";
    public static final String MENUS_CARTAS_SEGUNDOS = "mc_segundos";
    public static final String MENUS_CARTAS_POSTRES = "mc_postres";
    public static final String PROVEEDOR_CATEGORIA = "prov_categoria";

    //Campos Proveedores tabla
    //ID y NAME generales
    public static final String PROVEEDOR_TELEFONO = "prov_telefono";
    public static final String PROVEEDOR_DIRECCION = "prov_direccion";
    public static final String PROVEEDOR_EMAIL = "prov_email";

    //Campos Productos tabla
    //ID y NAME generales
    public static final String PRODUCTO_PROVEEDOR_ID = "product_provider_id";
    public static final String PRODUCTO_CATEGORIA_ID = "product_categoria_id";
    public static final String PRODUCTO_FORMATO_ID = "product_formato_id";
    public static final String PRODUCTO_FORMATO_NAME = "product_formato_name";
    public static final String PRODUCTO_CATEGORIA_NAME_ = "product_categoria_name";
    public static final String PRODUCTO_PROVEEDOR_NAME = "product_proveedor_name";
    public static final String INVENTARIO_ID = "inventario_id";

    //Campos Pedidos e inventarios listas tabla
    // PRODUCTO_CATEGORIA_ID PRODUCTO_FORMATO_ID tambien
    public static final String PEDIDO_ID = "pedido_id";
    public static final String PRODUCTO_ID = "producto_id";
    public static final String PRODUCTO_CANTIDAD = "producto_cantidad";
    private static final String PRODUCTO_CANTIDAD_MIN = "producto_cantidad_min";
    public static final String PROVEEDOR_ID = "proveedor_id";

    //Campos escandallos
    public static final String COSTE_TOTAL = "coste_total";


    //Campos escandallo product
    public static final String ESCANDALLO_ID = "Escandallo_id";
    public static final String ESCANDALLO_PRODUCT_COSTE = "Escandallo_product_coste";
    public static final String ESCANDALLO_PRODUCT_QUANTITY = "Escandallo_quantity";
    public static final String ESCANDALLO_PRODUCT_FORMAT = "Escandallo_format";
    public static final String ESCANDALLO_PRODUCT_COST_FOR_UNI = "Escandallo_cost_for_uni";

    //database version
    private static final int DATABASE_VERSION = 3;

    // Campos para tabla escandallos.
    private static final String SqlCreateTable_recetas = "CREATE TABLE IF NOT EXISTS "
            + TABLE_RECETAS + "(" + ID + " INTEGER PRIMARY KEY,"
            + NAME + " TEXT, "
            + RECETA_IMG + "  TEXT, "
            + RECETA_INGREDIENTES + "  TEXT, "
            + RECETA_ELABORACION + "  TEXT, "
            + RECETA_URL + "  TEXT, "
            + FECHA + " DEFAULT CURRENT_TIMESTAMP, "
            + RECETA_CATEGORIA + " TEXT)";

    private static final String SqlCreateTable_menus_cartas = "CREATE TABLE IF NOT EXISTS "
            + TABLE_MENUSCARTAS + "(" + ID + " INTEGER PRIMARY KEY,"
            + NAME + " TEXT, "
            + MENUS_CARTAS_ENTRANTES + "  TEXT, "
            + MENUS_CARTAS_PRIMEROS + "  TEXT, "
            + MENUS_CARTAS_SEGUNDOS + "  TEXT, "
            + MENUS_CARTAS_POSTRES + "  TEXT, "
            + COMENTARIO + "  TEXT, "
            + FECHA + " DEFAULT CURRENT_TIMESTAMP)";

    private static final String SqlCreateTable_providers = "CREATE TABLE IF NOT EXISTS "
            + TABLE_PROVEEDORES + "(" + ID + " INTEGER PRIMARY KEY,"
            + NAME + " TEXT, "
            + PROVEEDOR_TELEFONO + "  TEXT, "
            + PROVEEDOR_EMAIL + "  TEXT, "
            + PROVEEDOR_DIRECCION + "  TEXT, "
            + COMENTARIO + "  TEXT, "
            + PROVEEDOR_CATEGORIA + " TEXT)";//igual a productos categoria

    private static final String SqlCreateTable_pedidos = "CREATE TABLE IF NOT EXISTS "
            + TABLE_PEDIDOS + "(" + ID + " INTEGER PRIMARY KEY,"
            + NAME + " TEXT, "
            + PROVEEDOR_ID + " INTEGER, "
            + COMENTARIO + "  TEXT, "
            + FECHA + " DEFAULT CURRENT_TIMESTAMP)";

    private static final String SqlCreateTrigger_OnDeletePedido = " CREATE  TRIGGER IF NOT EXISTS ONDELETE_PEDIDO BEFORE DELETE "
            + " ON " + TABLE_PEDIDOS
            + " FOR EACH ROW "
            + " BEGIN "
            + " DELETE FROM " + TABLE_PEDIDOS_LISTAS + " WHERE " + PEDIDO_ID + " = OLD._id; "
            + " END; ";

    private static final String SqlCreateTrigger_OnDeleteInventario = " CREATE  TRIGGER IF NOT EXISTS ONDELETE_INVENTARIO BEFORE DELETE "
            + " ON " + TABLE_INVENTARIOS
            + " FOR EACH ROW "
            + " BEGIN "
            + " DELETE FROM " + TABLE_INVENTARIOS_LISTAS + " WHERE " + INVENTARIO_ID + " = OLD._id; "
            + " END; ";

    private static final String SqlCreateTable_pedidos_lista = "CREATE TABLE IF NOT EXISTS "
            + TABLE_PEDIDOS_LISTAS + "(" + ID + " INTEGER PRIMARY KEY,"
            + PEDIDO_ID + " INTEGER,"
            + PRODUCTO_ID + " INTEGER, "
            + PRODUCTO_CANTIDAD + "  INTEGER, "
            + PRODUCTO_CATEGORIA_ID + "  INTEGER, "
            + PRODUCTO_FORMATO_ID + " INTEGER)";

    private static final String SqlCreateTable_inventarios = "CREATE TABLE IF NOT EXISTS "
            + TABLE_INVENTARIOS + "(" + ID + " INTEGER PRIMARY KEY,"
            + NAME + " TEXT, "
            + COMENTARIO + "  TEXT, "
            + FECHA + " DEFAULT CURRENT_TIMESTAMP)";

    private static final String SqlCreateTable_invetarios_lista = "CREATE TABLE IF NOT EXISTS "
            + TABLE_INVENTARIOS_LISTAS + "(" + ID + " INTEGER PRIMARY KEY,"
            + INVENTARIO_ID + " INTEGER,"
            + PRODUCTO_ID + " INTEGER, "
            + PRODUCTO_CANTIDAD + "  INTEGER, "
            + PRODUCTO_CATEGORIA_ID + "  INTEGER, "
            + PRODUCTO_FORMATO_ID + " INTEGER)";

    private static final String SqlCreateTable_productos = "CREATE TABLE IF NOT EXISTS "
            + TABLE_PRODUCTOS + "(" + ID + " INTEGER PRIMARY KEY,"
            + NAME + " TEXT, "
            + PRODUCTO_FORMATO_NAME + " TEXT, "
            + PRODUCTO_CATEGORIA_NAME_ + " TEXT, "
            + PRODUCTO_PROVEEDOR_NAME + " TEXT, "
            + PRODUCTO_FORMATO_ID + " INTEGER, "
            + PRODUCTO_CATEGORIA_ID + " INTEGER, "
            + PRODUCTO_PROVEEDOR_ID + " INTEGER)";

    private static final String SqlCreateTable_productos_formato = "CREATE TABLE IF NOT EXISTS "
            + TABLE_PRODUCTOS_FORMATO + "(" + ID + " INTEGER PRIMARY KEY,"
            + NAME + " TEXT)";

    private static final String SqlCreateTable_productos_categoria = "CREATE TABLE IF NOT EXISTS "
            + TABLE_PRODUCTOS_CATEGORY + "(" + ID + " INTEGER PRIMARY KEY,"
            + NAME + " TEXT)";

    private static final String SqlCreateTable_categoria_recetas = "CREATE TABLE IF NOT EXISTS "
            + TABLE_RECETAS_CATEGORIA + "(" + ID + " INTEGER PRIMARY KEY,"
            + NAME + " TEXT)";


    // Update for version 2
    private final String SQLUpdate_Create_CampV2 = "ALTER TABLE " + TABLE_INVENTARIOS_LISTAS + " ADD COLUMN " + PRODUCTO_CANTIDAD_MIN + " INTEGER DEFAULT 0;";


    // Update version 3
    private final String SqlCreateTable_Escandallos = "CREATE TABLE IF NOT EXISTS "
            + TABLE_ESCANDALLOS + "(" + ID + " INTEGER PRIMARY KEY,"
            + NAME + " TEXT, "
            + COSTE_TOTAL + " REAL, "
            + COMENTARIO + " TEXT, "
            + FECHA + " DEFAULT CURRENT_TIMESTAMP)";

    private final String SqlCreateTable_Escadallo_products = "CREATE TABLE IF NOT EXISTS "
            + TABLE_ESCANDALLOS_PRODUCTS + "(" + ID + " INTEGER PRIMARY KEY,"
            + NAME + " TEXT, "
            + ESCANDALLO_ID + " INTEGER, "
            + ESCANDALLO_PRODUCT_QUANTITY + " TEXT, "
            + ESCANDALLO_PRODUCT_FORMAT + " TEXT, "
            + ESCANDALLO_PRODUCT_COST_FOR_UNI + " TEXT, "
            + ESCANDALLO_PRODUCT_COSTE + " REAL)";

    private static final String SqlCreateTrigger_OnDeleteEscandallo = " CREATE  TRIGGER IF NOT EXISTS ONDELETE_ESCANDALLO BEFORE DELETE "
            + " ON " + TABLE_ESCANDALLOS
            + " FOR EACH ROW "
            + " BEGIN "
            + " DELETE FROM " + TABLE_ESCANDALLOS_PRODUCTS + " WHERE " + ESCANDALLO_ID + " = OLD._id; "
            + " END; ";


    private final Context con;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.con = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        try {
            Resources res = con.getResources();
            ContentValues _Values = new ContentValues();
            db.execSQL(SqlCreateTable_recetas);
            db.execSQL(SqlCreateTable_menus_cartas);
            db.execSQL(SqlCreateTable_providers);
            db.execSQL(SqlCreateTable_inventarios);
            db.execSQL(SqlCreateTable_productos);
            db.execSQL(SqlCreateTable_pedidos);
            db.execSQL(SqlCreateTable_pedidos_lista);
            db.execSQL(SqlCreateTable_invetarios_lista);
            db.execSQL(SqlCreateTable_productos_formato);
            db.execSQL(SqlCreateTable_productos_categoria);
            db.execSQL(SqlCreateTable_categoria_recetas);
            db.execSQL(SqlCreateTable_Escandallos);
            db.execSQL(SqlCreateTable_Escadallo_products);
            db.execSQL(SqlCreateTrigger_OnDeletePedido);
            db.execSQL(SqlCreateTrigger_OnDeleteInventario);
            db.execSQL(SqlCreateTrigger_OnDeleteEscandallo);
            db.execSQL(SQLUpdate_Create_CampV2);

            int _Length;

            String[] arrayCat = res.getStringArray(R.array.categorys_products);
            String[] arrayFor = res.getStringArray(R.array.format_products);
            String[] arrayCatRecipes = res.getStringArray(R.array.categorys_recipes);

            _Length = arrayCat.length;
            for (int i = 0; i < _Length; i++) {
                _Values.put(NAME, arrayCat[i]);
                db.insert(TABLE_PRODUCTOS_CATEGORY, null, _Values);
            }
            _Length = arrayFor.length;
            for (int i = 0; i < _Length; i++) {
                _Values.put(NAME, arrayFor[i]);
                db.insert(TABLE_PRODUCTOS_FORMATO, null, _Values);
            }
            _Length = arrayCatRecipes.length;
            for (int i = 0; i < _Length; i++) {
                _Values.put(NAME, arrayCatRecipes[i]);
                db.insert(TABLE_RECETAS_CATEGORIA, null, _Values);
            }

        } catch (SQLException e) {
            Log.e("SqliteException: ", "getting exception "
                    + e.getLocalizedMessage());
        } catch (Exception e) {
            Log.e("Exception: ", "getting exception "
                    + e.getLocalizedMessage());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {

            onCreate(db);
            //update v2
            if (oldVersion < 2) {
                db.execSQL(SQLUpdate_Create_CampV2);
            }
            //update v3
            if (oldVersion < 3) {
                db.execSQL(SqlCreateTable_Escandallos);
                db.execSQL(SqlCreateTable_Escadallo_products);
                db.execSQL(SqlCreateTrigger_OnDeleteEscandallo);
            }

        } catch (SQLException e) {
            Log.e("SqliteException: ", "getting exception "
                    + e.getLocalizedMessage());
        } catch (Exception e) {
            Log.e("Exception: ", "getting exception "
                    + e.getLocalizedMessage());
        }
    }
}
