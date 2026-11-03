package com.sarvadiverse.husenhisab

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

// દૈનિક કામની એન્ટ્રી માટેનો ડેટા મોડેલ
data class DailyWorkEntry(
    val id: String = System.currentTimeMillis().toString(),
    val date: String = "",
    val lineName: String = "",
    val smallPoles: Int = 0,
    val largePoles: Int = 0,
    val smallPoleRate: Double = 0.0,
    val largePoleRate: Double = 0.0,
    val totalAmount: Double = 0.0,
    val note: String = ""
)

// લેજર/ચૂકવણીની એન્ટ્રી માટેનો ડેટા મોડેલ
data class LedgerEntry(
    val id: String = System.currentTimeMillis().toString(),
    val date: String = "",
    val personName: String = "",
    val amountPaid: Double = 0.0,
    val note: String = ""
)

class AppViewModel : ViewModel() {

    // કામની એન્ટ્રીઓનું લિસ્ટ
    private val _workEntries = MutableStateFlow<List<DailyWorkEntry>>(emptyList())
    val workEntries: StateFlow<List<DailyWorkEntry>> = _workEntries.asStateFlow()

    // ચૂકવણીઓનું લિસ્ટ
    private val _ledgerEntries = MutableStateFlow<List<LedgerEntry>>(emptyList())
    val ledgerEntries: StateFlow<List<LedgerEntry>> = _ledgerEntries.asStateFlow()

    // દૈનિક કામ ઉમેરો
    fun addWorkEntry(
        date: String,
        lineName: String,
        smallPoles: Int,
        largePoles: Int,
        smallPoleRate: Double,
        largePoleRate: Double,
        note: String
    ) {
        viewModelScope.launch {
            val total = (smallPoles * smallPoleRate) + (largePoles * largePoleRate)
            val newEntry = DailyWorkEntry(
                date = date,
                lineName = lineName,
                smallPoles = smallPoles,
                largePoles = largePoles,
                smallPoleRate = smallPoleRate,
                largePoleRate = largePoleRate,
                totalAmount = total,
                note = note
            )
            _workEntries.value = _workEntries.value + newEntry
        }
    }

    // ચૂકવણી/ખર્ચ ઉમેરો
    fun addLedgerEntry(date: String, personName: String, amount: Double, note: String) {
        viewModelScope.launch {
            val newEntry = LedgerEntry(
                date = date,
                personName = personName,
                amountPaid = amount,
                note = note
            )
            _ledgerEntries.value = _ledgerEntries.value + newEntry
        }
    }

    // કુલ કમાણી
    fun getTotalEarnings(): Double {
        return _workEntries.value.sumOf { it.totalAmount }
    }

    // કુલ ચૂકવેલ રકમ
    fun getTotalPaid(): Double {
        return _ledgerEntries.value.sumOf { it.amountPaid }
    }

    // બાકી નફો / બેલેન્સ
    fun getNetBalance(): Double {
        return getTotalEarnings() - getTotalPaid()
    }
}
