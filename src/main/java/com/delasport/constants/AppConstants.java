package com.delasport.constants;

public class AppConstants {

  public static final String FAILED = "FAILED";
  public static final String SUCCESSFUL = "SUCCESSFUL";

  public static final String EVENTS = "events";
  public static final String LEAGUES = "leagues";

  // Aggregation Stage Operators
  public static final String STAGE_MONGO_MATCH = "$match";
  public static final String STAGE_MONGO_GROUP = "$group";
  public static final String STAGE_MONGO_SORT = "$sort";
  public static final String STAGE_MONGO_PROJECT = "$project";
  public static final String STAGE_MONGO_LIMIT = "$limit";
  public static final String STAGE_MONGO_SKIP = "$skip";
  public static final String STAGE_MONGO_UNWIND = "$unwind";
  public static final String STAGE_MONGO_LOOKUP = "$lookup";
  public static final String STAGE_MONGO_ADD_FIELDS = "$addFields";
  public static final String STAGE_MONGO_SET = "$set";
  public static final String STAGE_MONGO_REPLACE_ROOT = "$replaceRoot";
  public static final String STAGE_MONGO_FACET = "$facet";
  public static final String STAGE_MONGO_BUCKET = "$bucket";
  public static final String STAGE_MONGO_COUNT = "$count";
  public static final String STAGE_MONGO_MERGE = "$merge";

  // Accumulator Operators for $group
  public static final String ACCUMULATOR_SUM = "$sum";
  public static final String ACCUMULATOR_AVG = "$avg";
  public static final String ACCUMULATOR_MIN = "$min";
  public static final String ACCUMULATOR_MAX = "$max";
  public static final String ACCUMULATOR_FIRST = "$first";
  public static final String ACCUMULATOR_LAST = "$last";
  public static final String ACCUMULATOR_PUSH = "$push";
  public static final String ACCUMULATOR_ADD_TO_SET = "$addToSet";
  public static final String ACCUMULATOR_STD_DEV_POP = "$stdDevPop";
  public static final String ACCUMULATOR_STD_DEV_SAMP = "$stdDevSamp";

  // Arithmetic Operators
  public static final String OPERATOR_ADD = "$add";
  public static final String OPERATOR_SUBTRACT = "$subtract";
  public static final String OPERATOR_MULTIPLY = "$multiply";
  public static final String OPERATOR_DIVIDE = "$divide";
  public static final String OPERATOR_MODULO = "$mod";

  // String Operators
  public static final String OPERATOR_CONCATENATE = "$concat";
  public static final String OPERATOR_SUBSTRING = "$substr";
  public static final String OPERATOR_TO_UPPERCASE = "$toUpper";
  public static final String OPERATOR_TO_LOWERCASE = "$toLower";
  public static final String OPERATOR_SPLIT_STRING = "$split";
  public static final String OPERATOR_STRING_LENGTH = "$strLenCP";
  public static final String OPERATOR_REGEX_MATCH = "$regexMatch";

  // Boolean Operators
  public static final String OPERATOR_AND = "$and";
  public static final String OPERATOR_OR = "$or";
  public static final String OPERATOR_NOT = "$not";

  // Comparison Operators
  public static final String OPERATOR_EQUAL = "$eq";
  public static final String OPERATOR_NOT_EQUAL = "$ne";
  public static final String OPERATOR_GREATER_THAN = "$gt";
  public static final String OPERATOR_GREATER_THAN_EQUALS = "$gte";
  public static final String OPERATOR_LESS_THAN = "$lt";
  public static final String OPERATOR_LESS_THAN_EQUALS = "$lte";
  public static final String OPERATOR_IN_LIST = "$in";
  public static final String OPERATOR_NOT_IN_LIST = "$nin";

  // Date Operators
  public static final String OPERATOR_DATE_TO_STRING = "$dateToString";
  public static final String OPERATOR_DATE_FROM_STRING = "$dateFromString";
  public static final String OPERATOR_DAY_OF_WEEK = "$dayOfWeek";
  public static final String OPERATOR_DAY_OF_MONTH = "$dayOfMonth";
  public static final String OPERATOR_DAY_OF_YEAR = "$dayOfYear";
  public static final String OPERATOR_MONTH = "$month";
  public static final String OPERATOR_YEAR = "$year";
  public static final String OPERATOR_HOUR = "$hour";
  public static final String OPERATOR_MINUTE = "$minute";
  public static final String OPERATOR_SECOND = "$second";
  public static final String OPERATOR_MILLISECOND = "$millisecond";
  public static final String OPERATOR_WEEK = "$week";

  // Array Operators
  public static final String OPERATOR_ARRAY_SIZE = "$size";
  public static final String OPERATOR_ARRAY_SLICE = "$slice";
  public static final String OPERATOR_ARRAY_ELEMENT_AT = "$arrayElemAt";
  public static final String OPERATOR_CONCATENATE_ARRAYS = "$concatArrays";
  public static final String OPERATOR_FILTER_ARRAY = "$filter";
  public static final String OPERATOR_MAP_ARRAY = "$map";
  public static final String OPERATOR_REDUCE_ARRAY = "$reduce";

  // Miscellaneous Operators
  public static final String OPERATOR_TYPE_CHECK = "$type";
  public static final String OPERATOR_CONDITIONAL = "$cond";
  public static final String OPERATOR_IF_NULL = "$ifNull";
  public static final String OPERATOR_SWITCH_CASE = "$switch";
  public static final String OPERATOR_MERGE_OBJECTS = "$mergeObjects";
}
