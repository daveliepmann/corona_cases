(ns corona.accidents
  "https://en.wikipedia.org/wiki/List_of_countries_by_traffic-related_death_rate"
  (:require
   [clojure.string :as s]
   [corona.core :as c :refer [in?]]
   [corona.countries :as co]))

(def death-rates
  "
  0 Country
  1 Continent
  2 Road fatalities per 100,000 inhabitants per year
  3 Road fatalities per 100,000 motor vehicles
  4 Road fatalities per 1 billion vehicle-km
  5 Total fatalities latest year (adjusted/estimated figures by WHO report)
  6 Year, data source (standard source: The WHO report 2015, data from 2013 The WHO report 2018, data from 2016
  "
  [
   ;; ["World"                            ""                  18.2     nil        nil     1350000    2016]
   ;; ["Africa"                           ""                  26.6     574        nil     246719     2016]
   ;; ["Eastern Mediterranean"            ""                  18.0     139        nil     122730     2016]
   ;; ["Western Pacific"                  ""                  16.9     69         nil     328591     2016]
   ;; ["South-east Asia"                  ""                  20.7     101        nil     316080     2016]
   ;; ["Americas"                         ""                  15.6     33         nil     153789     2016]
   ;; ["Europe"                           ""                  9        19         nil     85629      2018]
   ["Afghanistan"                      "Asia"              15.5     722.4      nil     4734       2013]
   ["Albania"                          "Europe"            13.6     107.2      nil     399        2018]
   ["Algeria"                          "Africa"            23.8     127.8      nil     9337       2013]
   ["Andorra"                          "Europe"            7.6      7.9        nil     6          2013]
   ["Angola"                           "Africa"            26.9     992        nil     5769       2013]
   ["Antigua and Barbuda"              "North America"     6.7      20         nil     6          2013]
   ["Argentina"                        "South America"     13.6     24.3       nil     5619       2013]
   ["Armenia"                          "Europe"            17.1     18.2       nil     499        2018]
   ["Australia"                        "Oceania"           5.6      7.4        5.2     1351       2016]
   ["Austria"                          "Europe"            5.2      6.1        5.1     452        2016]
   ["Azerbaijan"                       "Asia"              8.7      83         nil     845        2018]
   ["Bahamas"                          "North America"     13.8     36         nil     52         2013]
   ["Bahrain"                          "Asia"              5.2      14.3       nil     78         2017]
   ["Bangladesh"                       "Asia"              13.6     1020.6     nil     21316      2013]
   ["Barbados"                         "North America"     6.7      16.9       nil     19         2013]
   ["Belarus"                          "Europe"            13.7     32.9       nil     841        2018]
   ["Belgium"                          "Europe"            5.8      9          7.3     657        2016]
   ["Belize"                           "North America"     24.4     26         nil     81         2013]
   ["Benin"                            "Africa"            27.5     635.6      nil     2986       2016]
   ["Bhutan"                           "Asia"              15.1     167.2      nil     114        2013]
   ["Bolivia"                          "South America"     23.2     205.2      nil     3476       2013]
   ["Bosnia and Herzegovina"           "Europe"            15.7     76.7       nil     552        2018]
   ["Botswana"                         "Africa"            23.6     91.6       nil     477        2013]
   ["Brazil"                           "South America"     23.4     57.5       nil     46935      2013]
   ["Bulgaria"                         "Europe"            8.3      17.2       nil     601        2013]
   ["Burkina Faso"                     "Africa"            30.0     328.1      nil     5072       2013]
   ["Cambodia"                         "Asia"              17.4     107.2      nil     2635       2013]
   ["Cameroon"                         "Africa"            27.6     1385.1     nil     6136       2013]
   ["Canada"                           "North America"     5.8      8.9        5.1     2118       2016]
   ["Cape Verde"                       "Africa"            26.1     229        nil     130        2013]
   ["Central African Republic"         "Africa"            33.6     4484.4     nil     1546       2016]
   ["Chad"                             "Africa"            24.1     497        nil     3089       2013]
   ["Chile"                            "South America"     12.4     51.1       nil     2179       2013]
   ["China"                            "Asia"              18.2     104.5      nil     256180     2018]
   ["Colombia"                         "South America"     16.8     83.3       nil     8107       2013]
   ["Congo"                            "Africa"            26.4     1063       nil     1174       2013]
   ["Cook Islands"                     "Oceania"           24.2     40.2       nil     5          2013]
   ["Costa Rica"                       "North America"     13.9     38.4       nil     676        2013]
   ["Croatia"                          "Europe"            8.1      21.1       nil     340        2018]
   ["Cuba"                             "North America"     7.5      133.7      nil     840        2013]
   ["Cyprus"                           "Asia"              5.1      9.2        nil     60         2018]
   ["Czech Republic"                   "Europe"            5.9      8.6        11.5    630        2016]
   ["Democratic Republic of the Congo" "Africa"            33.7     nil        nil     26529      2016]
   ["Denmark"                          "Europe"            4.0      7.2        3.9     227        2016]
   ["Djibouti"                         "Africa"            24.7     nil        nil     216        2013]
   ["Dominica"                         "North America"     15.3     44.7       nil     11         2013]
   ["Dominican Republic"               "North America"     29.3     94.9       nil     3052       2013]
   ["Ecuador"                          "South America"     20.1     183.8      nil     3164       2013]
   ["Egypt"                            "Africa"            12.8     148.7      nil     10466      2013]
   ["El Salvador"                      "North America"     21.1     163.7      nil     1339       2013]
   ["Eritrea"                          "Africa"            24.1     2171.5     nil     1527       2013]
   ["Estonia"                          "Europe"            3.9      7.4        nil     52         2019]
   ["Ethiopia"                         "Africa"            26.7     385.7      nil     27326      2016]
   ["Fiji"                             "Oceania"           5.8      58.9       nil     51         2013]
   ["Finland"                          "Europe"            4.7      5          5.1     260        2016]
   ["France"                           "Europe"            5.5      8.4        5.8     3585       2016]
   ["Gabon"                            "Africa"            22.9     196.4      nil     383        2013]
   ["Gambia"                           "Africa"            29.4     998.7      nil     544        2013]
   ["Georgia"                          "Europe"            11.8     54         nil     514        2013]
   ["Germany"                          "Europe"            4.1      6.4        4.2     3327       2016]
   ["Ghana"                            "Africa"            26.2     443.1      nil     6789       2013]
   ["Greece"                           "Europe"            9.2      12.8       nil     1026       2016]
   ["Guatemala"                        "North America"     19.0     114.7      nil     2939       2013]
   ["Guinea"                           "Africa"            28.2     1343.7     nil     3490       2016]
   ["Guinea-Bissau"                    "Africa"            27.5     751.9      nil     468        2013]
   ["Guyana"                           "South America"     17.3     864.4      nil     138        2013]
   ["Honduras"                         "North America"     17.4     1021.7     nil     1408       2013]
   ["Hungary"                          "Europe"            7.8      20.7       nil     756        2018]
   ["Iceland"                          "Europe"            6.6      7.6        4.9     22         2016]
   ["India"                            "Asia"              22.6     130.1      nil     299091     2018]
   ["Indonesia"                        "Asia"              12.2     36.7       nil     31726      2018]
   ["Iran"                             "Asia"              20.5     54.1       nil     16426      2016]
   ["Iraq"                             "Asia"              20.2     151.2      nil     6826       2013]
   ["Ireland"                          "Europe"            4.1      7.5        3.8     194        2016]
   ["Israel"                           "Asia"              4.2      10.7       5.9     345        2016]
   ["Italy"                            "Europe"            5.6      6.3        nil     3333       2016]
   ["Ivory Coast"                      "Africa"            24.2     828.9      nil     4924       2013]
   ["Jamaica"                          "North America"     11.5     61.7       nil     320        2013]
   ["Japan"                            "Asia"              4.1      5.7        6.4     5224       2016]
   ["Jordan"                           "Asia"              26.3     151.4      nil     1913       2013]
   ["Kazakhstan"                       "Asia"              17.6     101.4      nil     3158       2018]
   ["Kenya"                            "Africa"            29.1     640.7      nil     12891      2013]
   ["Kiribati"                         "Oceania"           2.9      86.9       nil     3          2013]
   ["Kuwait"                           "Asia"              18.7     34.2       nil     629        2013]
   ["Kyrgyzstan"                       "Asia"              15.4     127.3      nil     916        2018]
   ["Laos"                             "Asia"              14.3     67.5       nil     971        2013]
   ["Latvia"                           "Europe"            9.3      24.8       nil     205        2013]
   ["Lebanon"                          "Asia"              8.9      64.8       nil     487        2019]
   ["Lesotho"                          "Africa"            28.2     474.8      nil     584        2013]
   ["Liberia"                          "Africa"            35.9     nil        nil     1657       2016]
   ["Libya"                            "Africa"            26.1     46.3       nil     1645       2016]
   ["Lithuania"                        "Europe"            8        16.1       nil     234        2018]
   ["Luxembourg"                       "Europe"            5.7      7.7        nil     36         2018]
   ["North Macedonia"                  "Europe"            6.4      49.1       nil     134        2018]
   ["Madagascar"                       "Africa"            28.4     2963       nil     6506       2013]
   ["Malawi"                           "Africa"            31.0     5601       nil     5601       2016]
   ["Malaysia"                         "Asia"              23.6     29.8       nil     7374       2016]
   ["Maldives"                         "Asia"              3.5      19.5       nil     12         2013]
   ["Mali"                             "Africa"            25.6     1352.5     nil     3920       2013]
   ["Malta"                            "Europe"            6.1      6.8        nil     26         2018]
   ["Marshall Islands"                 "Oceania"           5.7      141.8      nil     3          2013]
   ["Mauritania"                       "Africa"            24.5     228.7      nil     952        2013]
   ["Mauritius"                        "Africa"            12.2     35.6       nil     158        2013]
   ["Mexico"                           "North America"     12.3     43         27.5    15062      2013]
   ["Federated States of Micronesia"   "Oceania"           1.9      24         nil     2          2013]
   ["Monaco"                           "Europe"            0        nil        nil     0          2013]
   ["Mongolia"                         "Asia"              16.5     88.4       nil     499        2018]
   ["Montenegro"                       "Europe"            10.7     31.7       nil     67         2016]
   ["Morocco"                          "Africa"            18.0     209        nil     6870       2013]
   ["Mozambique"                       "Africa"            31.6     1507       nil     8173       2013]
   ["Myanmar"                          "Asia"              20.3     250.8      nil     10809      2013]
   ["Namibia"                          "Africa"            23.9     196.4      nil     551        2013]
   ["Nepal"                            "Asia"              17.0     399.8      nil     4713       2013]
   ["Netherlands"                      "Europe"            3.8      6          4.7     648        2016]
   ["New Zealand"                      "Oceania"           7.8      10         7.2     364        2016]
   ["Nicaragua"                        "North America"     15.3     164.3      nil     931        2013]
   ["Niger"                            "Africa"            26.4     1491.1     nil     4706       2013]
   ["Nigeria"                          "Africa"            20.5     615.4      nil     35621      2013]
   ["Norway"                           "Europe"            2.1      3          3.0     110        2019]
   ["Oman"                             "Asia"              25.4     85.3       nil     924        2013]
   ["Pakistan"                         "Asia"              14.2     283.9      nil     25781      2013]
   ["Palau"                            "Oceania"           4.8      14.1       nil     1          2013]
   ["Panama"                           "North America"     10.0     38.4       nil     386        2013]
   ["Papua New Guinea"                 "Oceania"           16.8     1306.5     nil     1232       2013]
   ["Paraguay"                         "South America"     20.7     114.7      nil     1408       2013]
   ["Peru"                             "South America"     13.9     99.3       nil     4234       2013]
   ["Philippines"                      "Asia"              10.5     135        nil     10379      2013]
   ["Poland"                           "Europe"            9.7      13.5       nil     3698       2016]
   ["Portugal"                         "Europe"            7.4      11.7       nil     768        2016]
   ["Qatar"                            "Asia"              15.2     50.9       nil     330        2013]
   ["Republic of Moldova"              "Europe"            9.7      61.8       nil     394        2018]
   ["Romania"                          "Europe"            8.7      31.4       nil     1881       2013]
   ["Russia"                           "Europe"            18.0     48.1       nil     25969      2016]
   ["Rwanda"                           "Africa"            32.1     3521.1     nil     3782       2013]
   ["Saint Lucia"                      "North America"     18.1     2103.3     nil     33         2013]
   ["Saint Vincent and the Grenadines" "North America"     8.2      31.7       nil     9          2013]
   ["Samoa"                            "Oceania"           15.8     171.9      nil     30         2013]
   ["San Marino"                       "Europe"            3.2      1.8        nil     1          2013]
   ["São Tomé and Príncipe"            "Africa"            27.5     161.5      nil     55         2016]
   ["Saudi Arabia"                     "Asia"              27.4     119.7      nil     7898       2013]
   ["Senegal"                          "Africa"            27.2     956.4      nil     3844       2013]
   ["Serbia"                           "Europe"            7.4      28.4       nil     649        2016]
   ["Seychelles"                       "Africa"            8.6      43         nil     8          2013]
   ["Sierra Leone"                     "Africa"            27.3     2414.2     nil     1661       2013]
   ["Singapore"                        "Asia"              3.6      20.2       nil     197        2013]
   ["Slovakia"                         "Europe"            6.1      12.7       nil     330        2016]
   ["Slovenia"                         "Europe"            6.4      9.5        7.0     134        2016]
   ["Solomon Islands"                  "Oceania"           19.2     240        nil     108        2013]
   ["Somalia"                          "Africa"            25.4     6532.5     nil     3884       2016]
   ["South Africa"                     "Africa"            25.1     133.9      nil     13273      2013]
   ["South Korea"                      "Asia"              9.8      19.4       13.8    4990       2016]
   ["Spain"                            "Europe"            4.1      5.8        nil     1922       2016]
   ["Sri Lanka"                        "Asia"              17.4     70.9       nil     3691       2013]
   ["Sudan"                            "Africa"            24.3     2872.8     nil     9221       2013]
   ["Suriname"                         "South America"     19.1     49.7       nil     103        2013]
   ["Swaziland"                        "Africa"            24.2     1667.4     nil     303        2013]
   ["Sweden"                           "Europe"            2.8      4.6        3.3     278        2016]
   ["Switzerland"                      "Europe"            2.7      3.7        3.2     223        2016]
   ["Taiwan"                           "Asia"              12.4     nil        nil     2920       2016]
   ["Tajikistan"                       "Asia"              18.1     374.9      nil     1577       2018]
   ["Tanzania"                         "Africa"            32.9     1073.7     nil     16211      2013]
   ["Thailand"                         "Asia"              32.7     60.2       nil     22491      2016]
   ["Timor-Leste"                      "Asia"              16.6     295.8      nil     188        2013]
   ["Togo"                             "Africa"            31.1     3653.4     nil     2123       2013]
   ["Tonga"                            "Oceania"           7.6      98.1       nil     8          2013]
   ["Trinidad and Tobago"              "North America"     14.1     58.9       nil     189        2013]
   ["Tunisia"                          "Africa"            24.4     154.4      nil     2679       2013]
   ["Turkey"                           "Asia"              12.3     46.4       nil     9782       2016]
   ["Turkmenistan"                     "Asia"              14.5     107.8      nil     823        2018]
   ["Uganda"                           "Africa"            27.4     836.8      nil     10280      2013]
   ["Ukraine"                          "Europe"            13.7     42.2       nil     6089       2016]
   ["United Arab Emirates"             "Asia"              18.1     62.7       nil     1678       2013]
   ["United Kingdom"                   "Europe"            3.1      5.7        3.4     2019       2016]
   ["United States"                    "North America"     12.4     14.2       7.3     39888      2018]
   ["Uruguay"                          "South America"     13.4     19.6       nil     460        2016]
   ["Uzbekistan"                       "Asia"              11.5     nil        nil     3617       2016]
   ["Vanuatu"                          "Oceania"           16.6     300        nil     42         2013]
   ["Vietnam"                          "Asia"              24.5     55         nil     22419      2013]
   ["Yemen"                            "Asia"              21.5     436.6      nil     5248       2013]
   ["Zambia"                           "Africa"            24.7     670.9      nil     3586       2013]
   ["Zimbabwe"                         "Africa"            28.2     429.8      nil     3985       2013]
   ])

(def country--name-code-hm
  {
   "Afghanistan" "AF"
   "Albania" "AL"
   "Algeria" "DZ"
   "Andorra" "AD"
   "Angola" "AO"
   "Antigua and Barbuda" "AG"
   "Argentina" "AR"
   "Armenia" "AM"
   "Australia" "AU"
   "Austria" "AT"
   "Azerbaijan" "AZ"
   "Bahamas" "BS"
   "Bahrain" "BH"
   "Bangladesh" "BD"
   "Barbados" "BB"
   "Belarus" "BY"
   "Belgium" "BE"
   "Belize" "BZ"
   "Benin" "BJ"
   "Bhutan" "BT"
   "Bolivia" "BO"
   "Bosnia and Herzegovina" "BA"
   "Botswana" "BW"
   "Brazil" "BR"
   "Bulgaria" "BG"
   "Burkina Faso" "BF"
   "Cambodia" "KH"
   "Cameroon" "CM"
   "Canada" "CA"
   "Cape Verde" "CV"
   "Central African Republic" "CF"
   "Chad" "TD"
   "Chile" "CL"
   "China" "CN"
   "Colombia" "CO"
   "Congo" "CG"
   "Cook Islands" "CK"
   "Costa Rica" "CR"
   "Croatia" "HR"
   "Cuba" "CU"
   "Cyprus" "CY"
   "Czech Republic" "CZ"
   "Democratic Republic of the Congo" "CD"
   "Denmark" "DK"
   "Djibouti" "DJ"
   "Dominica" "DM"
   "Dominican Republic" "DO"
   "Ecuador" "EC"
   "Egypt" "EG"
   "El Salvador" "SV"
   "Eritrea" "ER"
   "Estonia" "EE"
   "Ethiopia" "ET"
   "Fiji" "FJ"
   "Finland" "FI"
   "France" "FR"
   "Gabon" "GA"
   "Gambia" "GM"
   "Georgia" "GE"
   "Germany" "DE"
   "Ghana" "GH"
   "Greece" "GR"
   "Guatemala" "GT"
   "Guinea" "GN"
   "Guinea-Bissau" "GW"
   "Guyana" "GY"
   "Honduras" "HN"
   "Hungary" "HU"
   "Iceland" "IS"
   "India" "IN"
   "Indonesia" "ID"
   "Iran" "IR"
   "Iraq" "IQ"
   "Ireland" "IE"
   "Israel" "IL"
   "Italy" "IT"
   "Ivory Coast" "CI"
   "Jamaica" "JM"
   "Japan" "JP"
   "Jordan" "JO"
   "Kazakhstan" "KZ"
   "Kenya" "KE"
   "Kiribati" "KI"
   "Kuwait" "KW"
   "Kyrgyzstan" "KG"
   "Laos" "LA"
   "Latvia" "LV"
   "Lebanon" "LB"
   "Lesotho" "LS"
   "Liberia" "LR"
   "Libya" "LY"
   "Lithuania" "LT"
   "Luxembourg" "LU"
   "North Macedonia" "MK"
   "Madagascar" "MG"
   "Malawi" "MW"
   "Malaysia" "MY"
   "Maldives" "MV"
   "Mali" "ML"
   "Malta" "MT"
   "Marshall Islands" "MH"
   "Mauritania" "MR"
   "Mauritius" "MU"
   "Mexico" "MX"
   "Federated States of Micronesia" "FM"
   "Monaco" "MC"
   "Mongolia" "MN"
   "Montenegro" "ME"
   "Morocco" "MA"
   "Mozambique" "MZ"
   "Myanmar" "MM"
   "Namibia" "NA"
   "Nepal" "NP"
   "Netherlands" "NL"
   "New Zealand" "NZ"
   "Nicaragua" "NI"
   "Niger" "NE"
   "Nigeria" "NG"
   "Norway" "NO"
   "Oman" "OM"
   "Pakistan" "PK"
   "Palau" "PW"
   "Panama" "PA"
   "Papua New Guinea" "PG"
   "Paraguay" "PY"
   "Peru" "PE"
   "Philippines" "PH"
   "Poland" "PL"
   "Portugal" "PT"
   "Qatar" "QA"
   "Republic of Moldova" "MD"
   "Romania" "RO"
   "Russia" "RU"
   "Rwanda" "RW"
   "Saint Lucia" "LC"
   "Saint Vincent and the Grenadines" "VC"
   "Samoa" "WS"
   "San Marino" "SM"
   "São Tomé and Príncipe" "ST"
   "Saudi Arabia" "SA"
   "Senegal" "SN"
   "Serbia" "RS"
   "Seychelles" "SC"
   "Sierra Leone" "SL"
   "Singapore" "SG"
   "Slovakia" "SK"
   "Slovenia" "SI"
   "Solomon Islands" "SB"
   "Somalia" "SO"
   "South Africa" "ZA"
   "South Korea" "KR"
   "Spain" "ES"
   "Sri Lanka" "LK"
   "Sudan" "SD"
   "Suriname" "SR"
   "Swaziland" "SZ"
   "Sweden" "SE"
   "Switzerland" "CH"
   "Taiwan" "TW"
   "Tajikistan" "TJ"
   "Tanzania" "TZ"
   "Thailand" "TH"
   "Timor-Leste" "TL"
   "Togo" "TG"
   "Tonga" "TO"
   "Trinidad and Tobago" "TT"
   "Tunisia" "TN"
   "Turkey" "TR"
   "Turkmenistan" "TM"
   "Uganda" "UG"
   "Ukraine" "UA"
   "United Arab Emirates" "AE"
   "United Kingdom" "GB"
   "United States" "US"
   "Uruguay" "UY"
   "Uzbekistan" "UZ"
   "Vanuatu" "VU"
   "Vietnam" "VN"
   "Yemen" "YE"
   "Zambia" "ZM"
   "Zimbabwe" "ZW"
   })

(defn country--name-code []
  (->> death-rates
       (remove (fn [dr]
                 (in? ["World" "Africa" "Eastern Mediterranean"
                       "Western Pacific" "South-east Asia"
                       "Americas" "Europe"] dr)))
       (mapv (fn [[cn & rest]] [cn (co/country_code cn)]))))

(defn deaths
  "Number of deaths for a country code"
  [country-code]
  (let [country-name (get (clojure.set/map-invert country--name-code-hm)
                          country-code)]
    (nth (->> death-rates
              (filter (fn [[cn & rest]] (= country-name cn)))
              (reduce identity))
         5)))
