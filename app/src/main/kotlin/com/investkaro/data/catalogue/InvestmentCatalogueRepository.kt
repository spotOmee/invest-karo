package com.investkaro.data.catalogue

import com.investkaro.domain.catalogue.CalculationModelIds
import com.investkaro.domain.catalogue.CatalogueCategory
import com.investkaro.domain.catalogue.CatalogueItem
import com.investkaro.domain.catalogue.CatalogueStatus

/**
 * Seeded in-memory repository for the complete 72-item Indian investment catalogue.
 * Supports partial text search, acronym queries, category filtering, and honest model assignment.
 */
object InvestmentCatalogueRepository {

    val items: List<CatalogueItem> = listOf(
        // ==========================================
        // CATEGORY A: MUTUAL FUNDS (22 ITEMS)
        // ==========================================
        CatalogueItem(
            id = "mf_sip",
            name = "Mutual Fund SIP (market-linked estimate)",
            category = CatalogueCategory.MUTUAL_FUNDS,
            description = "Systematic Investment Plan with monthly end-of-period contributions.",
            modelId = CalculationModelIds.MONTHLY_CONTRIBUTION,
            riskAndLimitations = "Market-linked returns fluctuate daily based on NAV. Excludes taxes, expense ratios, and exit loads.",
            status = CatalogueStatus.CALCULATOR_SUPPORTED,
            searchKeywords = listOf("SIP", "MF", "Systematic", "Mutual Fund", "Equity SIP")
        ),
        CatalogueItem(
            id = "mf_lump_sum",
            name = "Mutual Fund lump sum",
            category = CatalogueCategory.MUTUAL_FUNDS,
            description = "One-time upfront investment in a mutual fund scheme.",
            modelId = CalculationModelIds.LUMP_SUM_COMPOUND,
            riskAndLimitations = "Subject to market entry point timing and daily NAV volatility.",
            status = CatalogueStatus.CALCULATOR_SUPPORTED,
            searchKeywords = listOf("Lump sum", "One-time", "MF", "Mutual Fund")
        ),
        CatalogueItem(
            id = "equity_mf",
            name = "Equity mutual fund",
            category = CatalogueCategory.MUTUAL_FUNDS,
            description = "Invests primarily in listed equities for long-term capital appreciation.",
            modelId = CalculationModelIds.MONTHLY_CONTRIBUTION,
            riskAndLimitations = "High market risk and short-term volatility. Returns are not guaranteed.",
            status = CatalogueStatus.CALCULATOR_SUPPORTED,
            searchKeywords = listOf("Equity", "Stocks MF", "Growth", "Shares")
        ),
        CatalogueItem(
            id = "debt_mf",
            name = "Debt mutual fund",
            category = CatalogueCategory.MUTUAL_FUNDS,
            description = "Invests in fixed income securities such as government bonds, debentures, and commercial paper.",
            modelId = CalculationModelIds.MONTHLY_CONTRIBUTION,
            riskAndLimitations = "Subject to interest rate risk, duration risk, and credit default risk. Taxed at marginal income tax slab.",
            status = CatalogueStatus.CALCULATOR_SUPPORTED,
            searchKeywords = listOf("Debt", "Bond", "Fixed Income MF")
        ),
        CatalogueItem(
            id = "hybrid_mf",
            name = "Hybrid mutual fund",
            category = CatalogueCategory.MUTUAL_FUNDS,
            description = "Allocates across both equities and fixed income securities for balanced risk.",
            modelId = CalculationModelIds.MONTHLY_CONTRIBUTION,
            riskAndLimitations = "Moderate volatility depending on equity-debt ratio. Rebalancing and taxation rules apply.",
            status = CatalogueStatus.CALCULATOR_SUPPORTED,
            searchKeywords = listOf("Hybrid", "Balanced", "Aggressive Hybrid", "Multi Asset")
        ),
        CatalogueItem(
            id = "index_sip",
            name = "Index fund SIP",
            category = CatalogueCategory.MUTUAL_FUNDS,
            description = "Passively replicates a benchmark index (e.g. Nifty 50 or Sensex) with low expense ratios.",
            modelId = CalculationModelIds.MONTHLY_CONTRIBUTION,
            riskAndLimitations = "Full market downside exposure; tracking error and cash drag may affect real returns.",
            status = CatalogueStatus.CALCULATOR_SUPPORTED,
            searchKeywords = listOf("Index", "Nifty 50", "Sensex", "Passive", "SIP")
        ),
        CatalogueItem(
            id = "index_lump_sum",
            name = "Index fund lump sum",
            category = CatalogueCategory.MUTUAL_FUNDS,
            description = "One-time passive investment into an index-replicating fund.",
            modelId = CalculationModelIds.LUMP_SUM_COMPOUND,
            riskAndLimitations = "Timing risk on lump-sum entry into equity market indices.",
            status = CatalogueStatus.CALCULATOR_SUPPORTED,
            searchKeywords = listOf("Index", "Passive", "Lump sum")
        ),
        CatalogueItem(
            id = "elss_mf",
            name = "ELSS tax-saving fund",
            category = CatalogueCategory.MUTUAL_FUNDS,
            description = "Equity Linked Savings Scheme qualifying for Section 80C tax deduction with a mandatory 3-year lock-in.",
            modelId = null,
            riskAndLimitations = "Mandatory 3-year lock-in per installment. Requires dedicated lock-in and tax modelling.",
            status = CatalogueStatus.PLANNING_ONLY,
            searchKeywords = listOf("ELSS", "80C", "Tax saving", "Lock-in", "Deduction")
        ),
        CatalogueItem(
            id = "flexi_cap_mf",
            name = "Flexi-cap fund",
            category = CatalogueCategory.MUTUAL_FUNDS,
            description = "Dynamic equity fund that can invest across large, mid, and small cap companies without market cap restrictions.",
            modelId = CalculationModelIds.MONTHLY_CONTRIBUTION,
            riskAndLimitations = "Equity market volatility and fund manager allocation discretion.",
            status = CatalogueStatus.CALCULATOR_SUPPORTED,
            searchKeywords = listOf("Flexi cap", "Flexicap", "All cap")
        ),
        CatalogueItem(
            id = "large_cap_mf",
            name = "Large-cap fund",
            category = CatalogueCategory.MUTUAL_FUNDS,
            description = "Invests at least 80% in top 100 established companies by market capitalization.",
            modelId = CalculationModelIds.MONTHLY_CONTRIBUTION,
            riskAndLimitations = "Lower volatility than mid/small caps, but remains susceptible to broad market downturns.",
            status = CatalogueStatus.CALCULATOR_SUPPORTED,
            searchKeywords = listOf("Large cap", "Bluechip", "Top 100")
        ),
        CatalogueItem(
            id = "mid_cap_mf",
            name = "Mid-cap fund",
            category = CatalogueCategory.MUTUAL_FUNDS,
            description = "Invests at least 65% in companies ranked 101st to 250th by market cap.",
            modelId = CalculationModelIds.MONTHLY_CONTRIBUTION,
            riskAndLimitations = "Higher volatility and liquidity risk during market corrections.",
            status = CatalogueStatus.CALCULATOR_SUPPORTED,
            searchKeywords = listOf("Mid cap", "Midcap", "Growth")
        ),
        CatalogueItem(
            id = "small_cap_mf",
            name = "Small-cap fund",
            category = CatalogueCategory.MUTUAL_FUNDS,
            description = "Invests at least 65% in companies ranked 251st and beyond by market cap.",
            modelId = CalculationModelIds.MONTHLY_CONTRIBUTION,
            riskAndLimitations = "Substantial price volatility, business failure risk, and severe drawdowns during bear markets.",
            status = CatalogueStatus.CALCULATOR_SUPPORTED,
            searchKeywords = listOf("Small cap", "Smallcap", "High return")
        ),
        CatalogueItem(
            id = "multi_cap_mf",
            name = "Multi-cap fund",
            category = CatalogueCategory.MUTUAL_FUNDS,
            description = "SEBI mandated minimum 25% allocation each in large caps, mid caps, and small caps.",
            modelId = CalculationModelIds.MONTHLY_CONTRIBUTION,
            riskAndLimitations = "Forced allocation to small/mid caps even in hostile market conditions.",
            status = CatalogueStatus.CALCULATOR_SUPPORTED,
            searchKeywords = listOf("Multi cap", "Multicap", "25-25-25")
        ),
        CatalogueItem(
            id = "large_mid_mf",
            name = "Large & mid-cap fund",
            category = CatalogueCategory.MUTUAL_FUNDS,
            description = "Mandated minimum 35% in large caps and 35% in mid caps.",
            modelId = CalculationModelIds.MONTHLY_CONTRIBUTION,
            riskAndLimitations = "Blended equity risk profile.",
            status = CatalogueStatus.CALCULATOR_SUPPORTED,
            searchKeywords = listOf("Large and mid", "Large mid")
        ),
        CatalogueItem(
            id = "sectoral_thematic_mf",
            name = "Sectoral/thematic fund",
            category = CatalogueCategory.MUTUAL_FUNDS,
            description = "Concentrated exposure to a single industry (e.g. Banking, IT, Pharma, Infrastructure).",
            modelId = CalculationModelIds.MONTHLY_CONTRIBUTION,
            riskAndLimitations = "Concentration risk. Severe underperformance when the sector falls out of favor.",
            status = CatalogueStatus.CALCULATOR_SUPPORTED,
            searchKeywords = listOf("Sectoral", "Thematic", "IT", "Pharma", "Banking")
        ),
        CatalogueItem(
            id = "international_mf",
            name = "International mutual fund",
            category = CatalogueCategory.MUTUAL_FUNDS,
            description = "Invests in global equities (e.g. US, Europe, Emerging Markets) providing currency diversification.",
            modelId = CalculationModelIds.MONTHLY_CONTRIBUTION,
            riskAndLimitations = "Currency exchange rate fluctuation and geopolitical risk. Taxed at slab rates in India.",
            status = CatalogueStatus.CALCULATOR_SUPPORTED,
            searchKeywords = listOf("International", "Global", "US stocks", "Nasdaq")
        ),
        CatalogueItem(
            id = "liquid_mf",
            name = "Liquid fund",
            category = CatalogueCategory.MUTUAL_FUNDS,
            description = "Invests in debt and money market securities with maturity up to 91 days.",
            modelId = null,
            riskAndLimitations = "Designed for capital preservation; returns track overnight money market rates.",
            status = CatalogueStatus.PLANNING_ONLY,
            searchKeywords = listOf("Liquid", "Cash equivalent", "Short term")
        ),
        CatalogueItem(
            id = "overnight_mf",
            name = "Overnight fund",
            category = CatalogueCategory.MUTUAL_FUNDS,
            description = "Invests in securities with residual maturity of exactly 1 day.",
            modelId = null,
            riskAndLimitations = "Minimal credit and duration risk; lowest yielding category in mutual funds.",
            status = CatalogueStatus.PLANNING_ONLY,
            searchKeywords = listOf("Overnight", "1-day", "CBLO", "TREPS")
        ),
        CatalogueItem(
            id = "money_market_mf",
            name = "Money market fund",
            category = CatalogueCategory.MUTUAL_FUNDS,
            description = "Invests in commercial papers, certificate of deposits, and T-bills up to 1-year maturity.",
            modelId = null,
            riskAndLimitations = "Short-duration credit and reinvestment risk.",
            status = CatalogueStatus.PLANNING_ONLY,
            searchKeywords = listOf("Money market", "CP", "CD", "Commercial paper")
        ),
        CatalogueItem(
            id = "gilt_mf",
            name = "Gilt fund",
            category = CatalogueCategory.MUTUAL_FUNDS,
            description = "Invests minimum 80% in sovereign government securities of varying maturities.",
            modelId = null,
            riskAndLimitations = "Zero credit risk but very high interest rate sensitivity/duration risk.",
            status = CatalogueStatus.PLANNING_ONLY,
            searchKeywords = listOf("Gilt", "G-Sec MF", "Sovereign debt")
        ),
        CatalogueItem(
            id = "target_maturity_mf",
            name = "Target maturity fund",
            category = CatalogueCategory.MUTUAL_FUNDS,
            description = "Passively managed debt index fund that holds securities matching a specified maturity year.",
            modelId = null,
            riskAndLimitations = "Yield predictable only if held till maturity.",
            status = CatalogueStatus.PLANNING_ONLY,
            searchKeywords = listOf("Target maturity", "Bharat Bond", "TMF")
        ),
        CatalogueItem(
            id = "fof_mf",
            name = "Fund of funds",
            category = CatalogueCategory.MUTUAL_FUNDS,
            description = "Invests in underlying domestic or overseas mutual funds rather than direct securities.",
            modelId = null,
            riskAndLimitations = "Dual layer of expense ratios and debt-like tax classification.",
            status = CatalogueStatus.PLANNING_ONLY,
            searchKeywords = listOf("FOF", "Fund of funds", "Multi manager")
        ),

        // ==========================================
        // CATEGORY B: SHARES, ETFS & LISTED (10 ITEMS)
        // ==========================================
        CatalogueItem(
            id = "direct_stocks",
            name = "Individual stocks/shares",
            category = CatalogueCategory.SHARES_ETFS,
            description = "Direct ownership of listed equities on BSE/NSE.",
            modelId = null,
            riskAndLimitations = "Non-linear returns. Risk of 100% principal loss in severe corporate failure. Requires active monitoring.",
            status = CatalogueStatus.PLANNING_ONLY,
            searchKeywords = listOf("Stocks", "Shares", "Direct equity", "BSE", "NSE")
        ),
        CatalogueItem(
            id = "index_etf",
            name = "Index ETF",
            category = CatalogueCategory.SHARES_ETFS,
            description = "Exchange-Traded Fund traded intraday on stock exchanges tracking an index.",
            modelId = null,
            riskAndLimitations = "Requires demat account. Subject to liquidity discount and exchange bid-ask spreads.",
            status = CatalogueStatus.PLANNING_ONLY,
            searchKeywords = listOf("ETF", "Index ETF", "NiftyBeES")
        ),
        CatalogueItem(
            id = "gold_etf",
            name = "Gold ETF",
            category = CatalogueCategory.SHARES_ETFS,
            description = "Units representing physical gold of 99.5% purity held in paper/demat format.",
            modelId = null,
            riskAndLimitations = "Tracks spot domestic gold prices. Demat charges and fund management fees apply.",
            status = CatalogueStatus.PLANNING_ONLY,
            searchKeywords = listOf("Gold ETF", "GoldBeES", "Precious metal")
        ),
        CatalogueItem(
            id = "silver_etf",
            name = "Silver ETF",
            category = CatalogueCategory.SHARES_ETFS,
            description = "Exchange traded instrument representing physical silver bullion.",
            modelId = null,
            riskAndLimitations = "Higher industrial volatility than gold.",
            status = CatalogueStatus.PLANNING_ONLY,
            searchKeywords = listOf("Silver ETF", "SilverBeES")
        ),
        CatalogueItem(
            id = "bond_etf",
            name = "Bond ETF",
            category = CatalogueCategory.SHARES_ETFS,
            description = "Exchange-traded basket of government securities or public sector bonds.",
            modelId = null,
            riskAndLimitations = "Market price on exchange may diverge from underlying NAV.",
            status = CatalogueStatus.PLANNING_ONLY,
            searchKeywords = listOf("Bond ETF", "G-Sec ETF", "Bharat Bond ETF")
        ),
        CatalogueItem(
            id = "dividend_stocks",
            name = "Dividend-focused stocks",
            category = CatalogueCategory.SHARES_ETFS,
            description = "Mature corporate stocks offering steady regular dividend yields.",
            modelId = null,
            riskAndLimitations = "Dividends are taxed at personal slab rates. Capital gains remain variable.",
            status = CatalogueStatus.PLANNING_ONLY,
            searchKeywords = listOf("Dividend", "PSU stocks", "Cash flow")
        ),
        CatalogueItem(
            id = "ipo_shares",
            name = "IPO/new share issue",
            category = CatalogueCategory.SHARES_ETFS,
            description = "Initial Public Offering subscription for unlisted companies entering public markets.",
            modelId = null,
            riskAndLimitations = "Allotment not guaranteed. Listing gains or losses unpredictable.",
            status = CatalogueStatus.PLANNING_ONLY,
            searchKeywords = listOf("IPO", "Initial Public Offering", "New issue")
        ),
        CatalogueItem(
            id = "sgb",
            name = "Sovereign Gold Bond (SGB)",
            category = CatalogueCategory.SHARES_ETFS,
            description = "RBI-issued sovereign paper paying 2.5% p.a. semi-annual interest plus gold market price at 8-year maturity.",
            modelId = null,
            riskAndLimitations = "Government does not issue fresh tranches continually. Capital gains tax exempt only at 8-year maturity.",
            status = CatalogueStatus.PLANNING_ONLY,
            searchKeywords = listOf("SGB", "Sovereign Gold", "RBI Gold", "2.5% gold")
        ),
        CatalogueItem(
            id = "reit",
            name = "REIT (Real Estate Investment Trust)",
            category = CatalogueCategory.SHARES_ETFS,
            description = "Listed trusts owning income-generating commercial real estate, distributing 90%+ net distributable cash flows.",
            modelId = null,
            riskAndLimitations = "Tenant vacancy risk, interest rate sensitivity, complex tax breakdown (dividend/interest/repayment).",
            status = CatalogueStatus.PLANNING_ONLY,
            searchKeywords = listOf("REIT", "Commercial real estate", "Embassy", "Mindspace")
        ),
        CatalogueItem(
            id = "invit",
            name = "InvIT (Infrastructure Investment Trust)",
            category = CatalogueCategory.SHARES_ETFS,
            description = "Listed trust holding operational infrastructure assets (toll roads, transmission lines, gas pipelines).",
            modelId = null,
            riskAndLimitations = "Concession period expiry risk, traffic volume variability, regulatory scrutiny.",
            status = CatalogueStatus.PLANNING_ONLY,
            searchKeywords = listOf("InvIT", "Infrastructure trust", "PowerGrid InvIT")
        ),

        // ==========================================
        // CATEGORY C: FIXED INCOME & SAVINGS (20 ITEMS)
        // ==========================================
        CatalogueItem(
            id = "bank_fd",
            name = "Bank fixed deposit (FD)",
            category = CatalogueCategory.FIXED_INCOME,
            description = "Term deposit offering guaranteed fixed rate with quarterly compounding under RBI rules.",
            modelId = null,
            riskAndLimitations = "Compounding is quarterly, not monthly. Subject to TDS and premature withdrawal penalty. Insured up to ₹5 Lakh by DICGC.",
            status = CatalogueStatus.PLANNING_ONLY,
            searchKeywords = listOf("FD", "Fixed Deposit", "Term Deposit", "Bank FD")
        ),
        CatalogueItem(
            id = "bank_rd",
            name = "Bank recurring deposit (RD)",
            category = CatalogueCategory.FIXED_INCOME,
            description = "Periodic monthly deposit with quarterly compounding interest.",
            modelId = null,
            riskAndLimitations = "Compounds quarterly under Indian banking rules (not monthly ordinary annuity). Requires dedicated quarterly model.",
            status = CatalogueStatus.PLANNING_ONLY,
            searchKeywords = listOf("RD", "Recurring Deposit", "Bank RD")
        ),
        CatalogueItem(
            id = "corp_fd",
            name = "Corporate FD",
            category = CatalogueCategory.FIXED_INCOME,
            description = "Fixed deposits offered by NBFCs and manufacturing corporations.",
            modelId = null,
            riskAndLimitations = "Not insured by DICGC. Credit rating downgrade or default risk possible.",
            status = CatalogueStatus.PLANNING_ONLY,
            searchKeywords = listOf("Corporate FD", "Company FD", "NBFC FD")
        ),
        CatalogueItem(
            id = "po_rd",
            name = "Post Office RD",
            category = CatalogueCategory.FIXED_INCOME,
            description = "5-year recurring deposit backed by Government of India.",
            modelId = null,
            riskAndLimitations = "Quarterly compounding. Quarterly government rate revisions apply to new deposits.",
            status = CatalogueStatus.PLANNING_ONLY,
            searchKeywords = listOf("Post office RD", "PO RD", "Dak")
        ),
        CatalogueItem(
            id = "po_td",
            name = "Post Office Time Deposit",
            category = CatalogueCategory.FIXED_INCOME,
            description = "Fixed term deposit for 1, 2, 3, or 5 years with annual interest payments calculated quarterly.",
            modelId = null,
            riskAndLimitations = "Interest payable annually; 5-year tenure qualifies under Section 80C.",
            status = CatalogueStatus.PLANNING_ONLY,
            searchKeywords = listOf("Time Deposit", "POTD", "PO FD")
        ),
        CatalogueItem(
            id = "po_mis",
            name = "Post Office Monthly Income Scheme (MIS)",
            category = CatalogueCategory.FIXED_INCOME,
            description = "5-year government deposit paying fixed monthly interest directly to depositor.",
            modelId = null,
            riskAndLimitations = "Maximum deposit limit ₹9 Lakh (single) / ₹15 Lakh (joint). No compounding on monthly payout.",
            status = CatalogueStatus.PLANNING_ONLY,
            searchKeywords = listOf("MIS", "Monthly Income Scheme", "Post Office MIS")
        ),
        CatalogueItem(
            id = "po_savings",
            name = "Post Office Savings Account",
            category = CatalogueCategory.FIXED_INCOME,
            description = "Liquid savings account with Department of Posts.",
            modelId = null,
            riskAndLimitations = "Statutory annual interest rate (currently 4.0% p.a.).",
            status = CatalogueStatus.PLANNING_ONLY,
            searchKeywords = listOf("PO Savings", "Post office account")
        ),
        CatalogueItem(
            id = "nsc",
            name = "National Savings Certificate (NSC)",
            category = CatalogueCategory.FIXED_INCOME,
            description = "5-year government savings bond with annual compounding paid at maturity.",
            modelId = null,
            riskAndLimitations = "5-year complete lock-in. Interest is deemed reinvested and eligible under Sec 80C.",
            status = CatalogueStatus.PLANNING_ONLY,
            searchKeywords = listOf("NSC", "National Savings", "80C Bond")
        ),
        CatalogueItem(
            id = "kvp",
            name = "Kisan Vikas Patra (KVP)",
            category = CatalogueCategory.FIXED_INCOME,
            description = "Small savings scheme that doubles invested amount over a government-prescribed tenure in months.",
            modelId = null,
            riskAndLimitations = "Tenure to double is revised periodically by Ministry of Finance.",
            status = CatalogueStatus.PLANNING_ONLY,
            searchKeywords = listOf("KVP", "Kisan Vikas", "Doubling scheme")
        ),
        CatalogueItem(
            id = "ppf",
            name = "Public Provident Fund (PPF)",
            category = CatalogueCategory.FIXED_INCOME,
            description = "15-year statutory scheme with EEE tax status. Interest calculated on lowest balance between 5th and month-end.",
            modelId = null,
            riskAndLimitations = "15-year lock-in. Annual compounding on March 31. Annual cap ₹1,50,000.",
            status = CatalogueStatus.PLANNING_ONLY,
            searchKeywords = listOf("PPF", "Public Provident", "15 year", "EEE")
        ),
        CatalogueItem(
            id = "ssy",
            name = "Sukanya Samriddhi Yojana (SSY)",
            category = CatalogueCategory.FIXED_INCOME,
            description = "Government welfare scheme for girl children up to age 10 with tax-free returns and annual compounding.",
            modelId = null,
            riskAndLimitations = "Restricted to girl children. 21-year maturity or marriage after age 18. Mandatory deposit for 15 years.",
            status = CatalogueStatus.PLANNING_ONLY,
            searchKeywords = listOf("SSY", "Sukanya", "Girl child scheme")
        ),
        CatalogueItem(
            id = "scss",
            name = "Senior Citizens Savings Scheme (SCSS)",
            category = CatalogueCategory.FIXED_INCOME,
            description = "Government scheme for individuals aged 60+ with quarterly interest disbursement.",
            modelId = null,
            riskAndLimitations = "Maximum deposit ₹30 Lakh. Quarterly interest is fully taxable.",
            status = CatalogueStatus.PLANNING_ONLY,
            searchKeywords = listOf("SCSS", "Senior citizen", "Retirement savings")
        ),
        CatalogueItem(
            id = "tbills",
            name = "Treasury bills (T-Bills)",
            category = CatalogueCategory.FIXED_INCOME,
            description = "Zero-coupon sovereign instruments issued at a discount for 91, 182, or 364 days.",
            modelId = null,
            riskAndLimitations = "Yield is derived from discount to face value; no periodic coupon payment.",
            status = CatalogueStatus.PLANNING_ONLY,
            searchKeywords = listOf("T-Bills", "Treasury", "91-day", "364-day", "RBI Retail Direct")
        ),
        CatalogueItem(
            id = "gsec",
            name = "Government securities (G-Secs)",
            category = CatalogueCategory.FIXED_INCOME,
            description = "Dated sovereign bonds with fixed semi-annual coupon payments.",
            modelId = null,
            riskAndLimitations = "Bond price moves inversely with macro interest rates; duration risk if sold before maturity.",
            status = CatalogueStatus.PLANNING_ONLY,
            searchKeywords = listOf("G-Sec", "GSec", "Central government bond", "RBI Retail")
        ),
        CatalogueItem(
            id = "sdl",
            name = "State Development Loans (SDLs)",
            category = CatalogueCategory.FIXED_INCOME,
            description = "Bonds issued by state governments, serviced through RBI with higher yield spread over central G-Secs.",
            modelId = null,
            riskAndLimitations = "Lower secondary market liquidity than central government securities.",
            status = CatalogueStatus.PLANNING_ONLY,
            searchKeywords = listOf("SDL", "State bond", "State development")
        ),
        CatalogueItem(
            id = "corp_bonds",
            name = "Corporate bonds",
            category = CatalogueCategory.FIXED_INCOME,
            description = "Debt instruments issued by public and private corporations.",
            modelId = null,
            riskAndLimitations = "Credit risk, credit rating downgrade risk, call/put options, and illiquid secondary trading.",
            status = CatalogueStatus.PLANNING_ONLY,
            searchKeywords = listOf("Bonds", "Corporate bond", "NCD", "Debenture")
        ),
        CatalogueItem(
            id = "tax_free_bonds",
            name = "Tax-free bonds",
            category = CatalogueCategory.FIXED_INCOME,
            description = "PSU bonds (e.g. NHAI, REC, PFC, HUDCO) whose annual interest is 100% tax exempt.",
            modelId = null,
            riskAndLimitations = "Only tradeable in secondary market with high ticket sizes; price fluctuates with market yields.",
            status = CatalogueStatus.PLANNING_ONLY,
            searchKeywords = listOf("Tax free", "Tax-free bonds", "NHAI", "REC")
        ),
        CatalogueItem(
            id = "frsb",
            name = "Floating Rate Savings Bonds (FRSB)",
            category = CatalogueCategory.FIXED_INCOME,
            description = "RBI 7-year taxable bonds with floating coupon pegged at 35 bps spread over prevailing NSC rate.",
            modelId = null,
            riskAndLimitations = "7-year lock-in. Coupon rate changes semi-annually. Non-transferable.",
            status = CatalogueStatus.PLANNING_ONLY,
            searchKeywords = listOf("FRSB", "Floating rate", "RBI bond")
        ),
        CatalogueItem(
            id = "sfb_fd",
            name = "Small Finance Bank FD",
            category = CatalogueCategory.FIXED_INCOME,
            description = "Term deposits with Small Finance Banks offering premium interest rates over universal banks.",
            modelId = null,
            riskAndLimitations = "DICGC coverage covers principal and interest up to ₹5 Lakh per depositor per bank.",
            status = CatalogueStatus.PLANNING_ONLY,
            searchKeywords = listOf("SFB", "Small finance bank", "High yield FD")
        ),
        CatalogueItem(
            id = "savings_interest",
            name = "Savings account interest",
            category = CatalogueCategory.FIXED_INCOME,
            description = "Interest earned on everyday bank savings balance.",
            modelId = null,
            riskAndLimitations = "Calculated on daily closing balance, credited quarterly. Section 80TTA exemption up to ₹10,000.",
            status = CatalogueStatus.PLANNING_ONLY,
            searchKeywords = listOf("Savings account", "Bank balance", "80TTA")
        ),

        // ==========================================
        // CATEGORY D: RETIREMENT & LONG-TERM (8 ITEMS)
        // ==========================================
        CatalogueItem(
            id = "epf",
            name = "EPF (Employees' Provident Fund)",
            category = CatalogueCategory.RETIREMENT,
            description = "Mandatory 12% salary contribution with employer matching, EPFO declared interest, and tax-free retirement corpus.",
            modelId = null,
            riskAndLimitations = "Requires complex salary component, contribution caps, and EPFO rate notifications.",
            status = CatalogueStatus.PLANNING_ONLY,
            searchKeywords = listOf("EPF", "PF", "Provident Fund", "UAN", "Salary PF")
        ),
        CatalogueItem(
            id = "vpf",
            name = "VPF (Voluntary Provident Fund)",
            category = CatalogueCategory.RETIREMENT,
            description = "Voluntary additional contribution beyond 12% EPF earning identical EPFO rate.",
            modelId = null,
            riskAndLimitations = "Locked in until retirement/resignation. Tax on interest if aggregate contribution exceeds ₹2.5L/year.",
            status = CatalogueStatus.PLANNING_ONLY,
            searchKeywords = listOf("VPF", "Voluntary PF")
        ),
        CatalogueItem(
            id = "nps",
            name = "National Pension System (NPS)",
            category = CatalogueCategory.RETIREMENT,
            description = "Tier 1 pension vehicle offering extra ₹50,000 tax deduction under Sec 80CCD(1B). Mandatory 40% annuity purchase at age 60.",
            modelId = null,
            riskAndLimitations = "Strict lock-in until age 60. Minimum 40% corpus must buy taxable life annuity.",
            status = CatalogueStatus.PLANNING_ONLY,
            searchKeywords = listOf("NPS", "National Pension", "80CCD", "PRAN", "Tier 1")
        ),
        CatalogueItem(
            id = "apy",
            name = "Atal Pension Yojana (APY)",
            category = CatalogueCategory.RETIREMENT,
            description = "Government pension scheme for unorganized sector guaranteeing ₹1,000–₹5,000 monthly pension from age 60.",
            modelId = null,
            riskAndLimitations = "Fixed contribution table based on entry age. Taxpayer exclusion rules apply.",
            status = CatalogueStatus.PLANNING_ONLY,
            searchKeywords = listOf("APY", "Atal Pension", "Guaranteed pension")
        ),
        CatalogueItem(
            id = "retirement_mf",
            name = "Retirement mutual fund",
            category = CatalogueCategory.RETIREMENT,
            description = "Solution-oriented mutual fund scheme with 5-year lock-in or until retirement age.",
            modelId = CalculationModelIds.MONTHLY_CONTRIBUTION,
            riskAndLimitations = "Mandatory 5-year lock-in. Market-linked equity and debt allocation.",
            status = CatalogueStatus.CALCULATOR_SUPPORTED,
            searchKeywords = listOf("Retirement MF", "Solution oriented", "Pension fund")
        ),
        CatalogueItem(
            id = "annuity_plan",
            name = "Pension/annuity plan",
            category = CatalogueCategory.RETIREMENT,
            description = "Insurance company annuity contract guaranteeing periodic lifetime cash flow.",
            modelId = null,
            riskAndLimitations = "Annuity rates are locked in; payouts are fully taxable as income.",
            status = CatalogueStatus.PLANNING_ONLY,
            searchKeywords = listOf("Annuity", "Life pension", "LIC", "Insurance pension")
        ),
        CatalogueItem(
            id = "child_education_goal",
            name = "Child education goal",
            category = CatalogueCategory.RETIREMENT,
            description = "Goal-oriented monthly compounding planning model for higher education funding.",
            modelId = CalculationModelIds.MONTHLY_CONTRIBUTION,
            riskAndLimitations = "Education inflation in India typically exceeds CPI. High equity allocation recommended for horizons >7 years.",
            status = CatalogueStatus.CALCULATOR_SUPPORTED,
            searchKeywords = listOf("Child education", "College fund", "Higher education")
        ),
        CatalogueItem(
            id = "goal_monthly_plan",
            name = "Goal-based monthly investment",
            category = CatalogueCategory.RETIREMENT,
            description = "Custom milestone planning (home down payment, marriage, dream sabbatical).",
            modelId = CalculationModelIds.MONTHLY_CONTRIBUTION,
            riskAndLimitations = "Assumes constant monthly compounding. Actual portfolio requires periodic rebalancing.",
            status = CatalogueStatus.CALCULATOR_SUPPORTED,
            searchKeywords = listOf("Goal", "Target", "Milestone", "Dream fund")
        ),

        // ==========================================
        // CATEGORY E: PRECIOUS METALS & PHYSICAL (7 ITEMS)
        // ==========================================
        CatalogueItem(
            id = "physical_gold",
            name = "Physical gold",
            category = CatalogueCategory.PRECIOUS_METALS,
            description = "Physical gold jewellery, coins, or bullion.",
            modelId = null,
            riskAndLimitations = "Making charges (8-25%), 3% GST, theft risk, locker fees, and purity deduction on resale.",
            status = CatalogueStatus.PLANNING_ONLY,
            searchKeywords = listOf("Gold", "Jewellery", "Karat", "Physical gold")
        ),
        CatalogueItem(
            id = "gold_coins",
            name = "Gold coins/bars",
            category = CatalogueCategory.PRECIOUS_METALS,
            description = "24K certified bullion coins and minted bars.",
            modelId = null,
            riskAndLimitations = "GST 3%, bank premium over spot price. Banks under RBI cannot repurchase sold coins.",
            status = CatalogueStatus.PLANNING_ONLY,
            searchKeywords = listOf("Gold bar", "Gold coin", "24 karat")
        ),
        CatalogueItem(
            id = "physical_silver",
            name = "Physical silver",
            category = CatalogueCategory.PRECIOUS_METALS,
            description = "Silver bars, utensils, and coins.",
            modelId = null,
            riskAndLimitations = "High bulk weight, tarnish risk, wide dealer spreads, and industrial cyclicality.",
            status = CatalogueStatus.PLANNING_ONLY,
            searchKeywords = listOf("Silver", "Chandi", "Silver bar")
        ),
        CatalogueItem(
            id = "real_estate",
            name = "Real estate/property",
            category = CatalogueCategory.PRECIOUS_METALS,
            description = "Residential or commercial physical land and built property.",
            modelId = null,
            riskAndLimitations = "Extremely illiquid, high transaction costs (stamp duty 5-8%, registration), title dispute risks.",
            status = CatalogueStatus.PLANNING_ONLY,
            searchKeywords = listOf("Real estate", "Property", "Flat", "Apartment")
        ),
        CatalogueItem(
            id = "rental_property",
            name = "Rental property",
            category = CatalogueCategory.PRECIOUS_METALS,
            description = "Physical property held for rental yield plus capital appreciation.",
            modelId = null,
            riskAndLimitations = "Net rental yield in Indian cities is typically low (2-3%). Tenant vacancy and maintenance deductions apply.",
            status = CatalogueStatus.PLANNING_ONLY,
            searchKeywords = listOf("Rental", "Rent", "Rental income")
        ),
        CatalogueItem(
            id = "agri_land",
            name = "Agricultural land",
            category = CatalogueCategory.PRECIOUS_METALS,
            description = "Farmland holdings in rural and semi-urban boundaries.",
            modelId = null,
            riskAndLimitations = "State-level legal restrictions on non-farmers purchasing farmland. Zero official liquidity.",
            status = CatalogueStatus.PLANNING_ONLY,
            searchKeywords = listOf("Agricultural", "Farmland", "Zameen")
        ),
        CatalogueItem(
            id = "reit_physical",
            name = "REIT-based real-estate exposure",
            category = CatalogueCategory.PRECIOUS_METALS,
            description = "Fractional, liquid commercial grade real estate exposure through SEBI-registered REITs.",
            modelId = null,
            riskAndLimitations = "Market volatility on exchange; distinct tax structure for distributions.",
            status = CatalogueStatus.PLANNING_ONLY,
            searchKeywords = listOf("REIT", "Fractional real estate")
        ),

        // ==========================================
        // CATEGORY F: OTHER & ADVANCED (5 ITEMS)
        // ==========================================
        CatalogueItem(
            id = "p2p_lending",
            name = "P2P lending",
            category = CatalogueCategory.OTHER_ADVANCED,
            description = "Peer-to-peer unsecured retail credit through RBI-regulated NBFC-P2P platforms.",
            modelId = null,
            riskAndLimitations = "High default rate risk on unsecured personal borrowers. Aggregate platform lending capped by RBI at ₹50 Lakh.",
            status = CatalogueStatus.PLANNING_ONLY,
            searchKeywords = listOf("P2P", "Peer to peer", "LendingClub", "Faircent")
        ),
        CatalogueItem(
            id = "startup_equity",
            name = "Startup/private equity",
            category = CatalogueCategory.OTHER_ADVANCED,
            description = "Early stage equity investment in unlisted private companies.",
            modelId = null,
            riskAndLimitations = "Extremely high failure rate (>80%). Complete illiquidity for 5-10 years until secondary or IPO.",
            status = CatalogueStatus.PLANNING_ONLY,
            searchKeywords = listOf("Startup", "Angel investment", "Private equity")
        ),
        CatalogueItem(
            id = "vc_funds",
            name = "Venture capital funds",
            category = CatalogueCategory.OTHER_ADVANCED,
            description = "SEBI Alternate Investment Funds (AIF Cat I / II).",
            modelId = null,
            riskAndLimitations = "Minimum ticket size ₹1 Crore. Multi-year capital drawdown commitments and 2/20 fee structures.",
            status = CatalogueStatus.PLANNING_ONLY,
            searchKeywords = listOf("VC", "AIF", "Venture capital")
        ),
        CatalogueItem(
            id = "commodity_investment",
            name = "Commodity investment",
            category = CatalogueCategory.OTHER_ADVANCED,
            description = "Energy, agriculture, and base metals holdings.",
            modelId = null,
            riskAndLimitations = "No cash flow or yield; exposed to global supply chain and geopolitics.",
            status = CatalogueStatus.PLANNING_ONLY,
            searchKeywords = listOf("Commodity", "Crude", "Copper", "Agri")
        ),
        CatalogueItem(
            id = "commodity_fo",
            name = "Commodity futures and options",
            category = CatalogueCategory.OTHER_ADVANCED,
            description = "Leveraged derivative contracts traded on MCX / NCDEX.",
            modelId = null,
            riskAndLimitations = "High leverage risk. SEBI study indicates over 90% of retail F&O traders lose money. Risk of total capital wipeout.",
            status = CatalogueStatus.PLANNING_ONLY,
            searchKeywords = listOf("F&O", "Futures", "Options", "Derivatives", "MCX")
        )
    )

    fun findById(id: String): CatalogueItem? =
        items.find { it.id == id }

    fun filter(query: String, category: CatalogueCategory? = null): List<CatalogueItem> {
        return items.filter { item ->
            val matchesCategory = category == null || item.category == category
            val matchesQuery = query.isBlank() || item.matchesQuery(query)
            matchesCategory && matchesQuery
        }
    }
}
