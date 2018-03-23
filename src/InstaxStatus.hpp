/** Namespace defining various status enums for use in interacting with the printer */

namespace InstaxCient {

  /**  Printer activity states */
  enum PRINTER_STATE {
    /** Printer is idle and ready for use*/
    PRINTER_IDLE,
    /** Printer is in an error state */
    PRINTER_ERROR,
    /** Printer is currently printing */
    PRINTER_PRINTING,
    /** Printer has finished printing and is ejecting */
    PRINTER_EJECTING
  };

  /** Printer battery states */
  enum BATT_STAT {
    /** The Printer is currently plugged in */
    BATT_PLUGGED,
    /** The printer does not have enough power to be used */
    BATT_EMPTY,
    /** The printer has one bar of energy */
    BATT_1,
    /** The printer has two bars of energy */
    BATT_2,
    /** The printer has 3 bars of energy (full) */
    BATT_3
  };

  /** Types of errors encountered. */
  enum INSTAX_ERRORS {
    /** No error detected. */
    E_NONE,
    /** Password is wrong */
    E_WRONG_PASSWORD,
    /** The film cartridge is empty. */
    E_FILM_EMPTY,
    /**The Film door is open */
    E_DOOR_OPEN,
    /** Low battery */
    E_LOW_BATTERY,
    /* Printer is printing */
    E_PRINTING,
    /* Printer is ejecting */
    E_EJECTING,
    /** Unknown error */
    E_UNKNOWN
  };

  /** Instax printer type */
  enum INSTAX_TYPE {
    SP1,
    SP2
  };

}
      
    
