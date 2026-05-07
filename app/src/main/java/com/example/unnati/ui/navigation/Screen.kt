package com.example.unnati.ui.navigation

sealed class Screen(val route: String) {
    object Splash       : Screen("splash")
    object PinLogin     : Screen("pin_login")
    object Dashboard    : Screen("dashboard")
    object MemberList   : Screen("member_list")
    object SavingsEntry : Screen("savings_entry")
    object LoanList     : Screen("loan_list")
    object Export       : Screen("export")
    object Settings     : Screen("settings")

    object AddMember : Screen("add_member/{memberId}") {
        fun createRoute(memberId: Int = -1) = "add_member/$memberId"
    }
    object MemberProfile : Screen("member_profile/{memberId}") {
        fun createRoute(memberId: Int) = "member_profile/$memberId"
    }
    object NewLoan : Screen("new_loan")
    object LoanDetail : Screen("loan_detail/{loanId}") {
        fun createRoute(loanId: Int) = "loan_detail/$loanId"
    }
}
