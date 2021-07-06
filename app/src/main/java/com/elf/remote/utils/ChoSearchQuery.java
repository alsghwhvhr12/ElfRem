package com.elf.remote.utils;

public class ChoSearchQuery {
    public static final int QUERY_DELIM = 39;//'

    public static final int HANGUL_BEGIN_UNICODE = 0xAC00; // 가
    public static final int HANGUL_END_UNICODE = 0xD7A3; // ?
    public static final int HANGUL_CHO_UNIT = 588; //한글 초성글자간 간격
    public static final int HANGUL_JUNG_UNIT = 28; //한글 중성글자간 간격

    public static final char[] CHO_LIST = {'ㄱ', 'ㄲ', 'ㄴ', 'ㄷ', 'ㄸ', 'ㄹ',
            'ㅁ', 'ㅂ', 'ㅃ', 'ㅅ', 'ㅆ', 'ㅇ', 'ㅈ', 'ㅉ', 'ㅊ', 'ㅋ', 'ㅌ', 'ㅍ', 'ㅎ'};
    public static final boolean[] CHO_SEARCH_LIST = {true, false, true, true, false, true,
            true, true, false, true, false, true, true, false, true, true, true, true, true};

    /**
     * 문자를 유니코드(10진수)로 변환 후 반환한다.
     *
     * @param ch 문자
     * @return 10진수 유니코드
     */
    public static int convertCharToUnicode(char ch) {
        return ch;
    }

    /**
     * 10진수를 16진수 문자열로 변환한다.
     *
     * @param decimal 10진수 숫자
     * @return 16진수 문자열
     */
    private static String toHexString(int decimal) {
        return Long.toHexString(decimal);
    }

    /**
     * 유니코드(16진수)를 문자로 변환 후 반환한다.
     *
     * @param hexUnicode Unicode Hex String
     * @return 문자값
     */
    public static char convertUnicodeToChar(String hexUnicode) {
        return (char) Integer.parseInt(hexUnicode, 16);
    }

    /**
     * 유니코드(10진수)를 문자로 변환 후 반환한다.
     */
    public static char convertUnicodeToChar(int unicode) {
        return convertUnicodeToChar(toHexString(unicode));
    }

    /**
     * 검색 문자열을 파싱해서 SQL Query 조건 문자열을 만든다.
     *
     * @param strSearch 검색 문자열
     * @return SQL Query 조건 문자열
     */
    public static String makeQuery(String strSearch, int country, String model, int min) {
        strSearch = strSearch == null ? "null" : strSearch.trim();

        StringBuilder retQuery = new StringBuilder();

        int nChoPosition;
        int nNextChoPosition;
        int StartUnicode;
        int EndUnicode;
        char NonCho = 0;

        int nQueryIndex = 0;
        StringBuilder query = new StringBuilder();
        for (int nIndex = 0; nIndex < strSearch.length(); nIndex++) {
            nChoPosition = -1;
            nNextChoPosition = -1;
            StartUnicode = -1;
            EndUnicode = -1;

            if (strSearch.charAt(nIndex) == QUERY_DELIM)
                continue;

            char cc = strSearch.charAt(nIndex);

            if (nQueryIndex != 0) {
                if ('ㆍ' != cc) query.append(" AND ");
                NonCho = cc;
            }

            for (int nChoIndex = 0; nChoIndex < CHO_LIST.length; nChoIndex++) {
                if (strSearch.charAt(nIndex) == CHO_LIST[nChoIndex]) {
                    nChoPosition = nChoIndex;
                    nNextChoPosition = nChoPosition + 1;
                    for (; nNextChoPosition < CHO_SEARCH_LIST.length; nNextChoPosition++) {
                        if (CHO_SEARCH_LIST[nNextChoPosition])
                            break;
                    }
                    break;
                }
            }
            int Unicode = convertCharToUnicode(strSearch.charAt(nIndex));
            if (nChoPosition >= 0) { //초성이 있을 경우
                StartUnicode = HANGUL_BEGIN_UNICODE + nChoPosition * HANGUL_CHO_UNIT;
                EndUnicode = HANGUL_BEGIN_UNICODE + nNextChoPosition * HANGUL_CHO_UNIT;
            } else {
                if (Unicode >= HANGUL_BEGIN_UNICODE && Unicode <= HANGUL_END_UNICODE) {
                    int Jong = ((Unicode - HANGUL_BEGIN_UNICODE) % HANGUL_CHO_UNIT) % HANGUL_JUNG_UNIT;

                    StartUnicode = Unicode;
                    if (Jong == 0) {// 초성+중성으로 되어 있는 경우
                        EndUnicode = Unicode + HANGUL_JUNG_UNIT;
                    } else {
                        EndUnicode = Unicode;
                    }
                }
            }

            if ('ㆍ' != cc) {
                if (StartUnicode > 0 && EndUnicode > 0) {
                    if (StartUnicode == EndUnicode)
                        query.append("UNICODE(substr(REPLACE(Title, ' ', ''),").append(nIndex + 1).append(",1))=UNICODE('").append(strSearch.charAt(nIndex)).append("')");
                    else if (EndUnicode == Unicode + HANGUL_JUNG_UNIT)
                        query.append("UNICODE(substr(REPLACE(Title, ' ', ''),").append(nIndex + 1).append(",1))=UNICODE('").append(strSearch.charAt(nIndex)).append("')");
                    else
                        query.append("(UNICODE(substr(REPLACE(Title, ' ', ''),").append(nIndex + 1).append(",1))>=UNICODE('").append(convertUnicodeToChar(StartUnicode))
                                .append("') AND UNICODE(substr(REPLACE(Title, ' ', ''),").append(nIndex + 1).append(",1))<UNICODE('").append(convertUnicodeToChar(EndUnicode)).append("'))");
                } else {
                    if (Character.isLowerCase(strSearch.charAt(nIndex))) { //영문 소문자
                        query.append("(UNICODE(substr(REPLACE(Title, ' ', ''),").append(nIndex + 1).append(",1))=UNICODE('").append(strSearch.charAt(nIndex)).append("')")
                                .append(" OR UNICODE(substr(REPLACE(Title, ' ', ''),").append(nIndex + 1).append(",1))=UNICODE('").append(Character.toUpperCase(strSearch.charAt(nIndex))).append("'))");
                    } else if (Character.isUpperCase(strSearch.charAt(nIndex))) { //영문 대문자
                        query.append("(UNICODE(substr(REPLACE(Title, ' ', ''),").append(nIndex + 1).append(",1))=UNICODE('").append(strSearch.charAt(nIndex)).append("')")
                                .append(" OR UNICODE(substr(REPLACE(Title, ' ', ''),").append(nIndex + 1).append(",1))=UNICODE('").append(Character.toLowerCase(strSearch.charAt(nIndex))).append("'))");
                    } else //기타 문자
                        query.append("UNICODE(substr(REPLACE(Title, ' ', ''),").append(nIndex + 1).append(",1))=UNICODE('").append(strSearch.charAt(nIndex)).append("')");
                }
            }
            nQueryIndex++;
        }

        if (query.length() > 0 && strSearch.trim().length() > 0) {
            retQuery.append("((").append(query.toString()).append(")");

            if (strSearch.contains(" ")) {
                retQuery.append("  OR (Idx like '").append(strSearch.replace(" ", "")).append("%' OR Idx Like '%,").append(strSearch.replace(" ", "")).append("%')) AND (Country = ").append(country).append(") AND (Model & ").append(model).append(")");
            } else if ('ㆍ' == NonCho) {
                retQuery.append(") AND (Country = ").append(country).append(") AND (Model & ").append(model).append(") LIMIT 300");
            } else {
                retQuery.append(" OR (Idx like '").append(strSearch).append("%' OR Idx Like '%,").append(strSearch).append("%')) AND (Country = ").append(country).append(") AND (Model & ").append(model).append(")");
            }

            retQuery.append(" ORDER BY INSTR(REPLACE(Title, ' ', ''), '").append(strSearch).append("') ASC");

            if (strSearch.length() == 1) retQuery.append(" LIMIT ").append(min).append(", 20");
        } else {
            retQuery.append(query.toString());
        }
        return retQuery.toString();
    }
}