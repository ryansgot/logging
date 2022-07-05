import Foundation
import fslogging

class SwiftEventLoggerExample: FSEventLogger {
    func addAttr(attrName: String, attrValue: String) {
        print("[printeventlogger] addAttr(attrName='\(attrName)'; attrValue='\(attrValue)')")
    }
    
    func addEvent(eventName: String, attrs: [String : String]) {
        print("[printeventlogger] addEvent(eventName='\(eventName)'; attrs='\(attrs)')")
    }
    
    func incrementAttrValue(attrName: String) {
        print("[printeventlogger] incrementAttrValue(attrName='\(attrName)')")
    }
    
    func removeAttr(attrName: String) {
        print("[printeventlogger] removeAttr(attrName='\(attrName)')")
    }
    
    func sendTimedOperation(operationName: String, startTimeMillis: Int64, endTimeMillis: Int64, durationAttrName: String?, startTimeMillisAttrName: String?, endTimeMillisAttrName: String?, startAttrs: [String : String], endAttrs: [String : String]) {
        print("[printeventlogger] sendTimedOperation(operationName='\(operationName)'; startTimeMillis=\(startTimeMillis); endTimeMillis=\(endTimeMillis); startTimeMillisAttrName='\(startTimeMillisAttrName)'; endTimeMillisAttrName='\(String(describing: endTimeMillisAttrName))'; startAttrs=\(startAttrs); endAttrs=\(endAttrs))")
    }
    
    func id() -> String {
        return "printeventlogger"
    }
    
    func runInTestEnvironment() -> Bool {
        return false
    }
}
