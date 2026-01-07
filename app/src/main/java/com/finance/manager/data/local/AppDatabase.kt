package com.finance.manager.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.finance.manager.data.local.dao.*
import com.finance.manager.data.local.entity.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        UserEntity::class,
        AccountEntity::class,
        CategoryEntity::class,
        TransactionEntity::class,
        BudgetEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun accountDao(): AccountDao
    abstract fun categoryDao(): CategoryDao
    abstract fun transactionDao(): TransactionDao
    abstract fun budgetDao(): BudgetDao


    class PrepopulateCallback(
        private val database: AppDatabase
    ) : Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)


            CoroutineScope(Dispatchers.IO).launch {
                prepopulateCategories(database)
            }
        }

        private suspend fun prepopulateCategories(db: AppDatabase) {
            val categories = listOf(

                CategoryEntity(name = "Їжа", type = "EXPENSE", icon = "restaurant", color = "#FF6B6B"),
                CategoryEntity(name = "Транспорт", type = "EXPENSE", icon = "directions_car", color = "#4ECDC4"),
                CategoryEntity(name = "Розваги", type = "EXPENSE", icon = "movie", color = "#95E1D3"),
                CategoryEntity(name = "Здоров'я", type = "EXPENSE", icon = "medical_services", color = "#F38181"),
                CategoryEntity(name = "Комунальні", type = "EXPENSE", icon = "home", color = "#AA96DA"),
                CategoryEntity(name = "Одяг", type = "EXPENSE", icon = "checkroom", color = "#FCBAD3"),
                CategoryEntity(name = "Інше", type = "EXPENSE", icon = "more_horiz", color = "#A8E6CF"),


                CategoryEntity(name = "Зарплата", type = "INCOME", icon = "payments", color = "#4CAF50"),
                CategoryEntity(name = "Підробіток", type = "INCOME", icon = "work", color = "#8BC34A"),
                CategoryEntity(name = "Подарунок", type = "INCOME", icon = "card_giftcard", color = "#CDDC39"),
                CategoryEntity(name = "Інше", type = "INCOME", icon = "more_horiz", color = "#9CCC65")
            )

            db.categoryDao().insertAll(categories)
        }
    }
}
