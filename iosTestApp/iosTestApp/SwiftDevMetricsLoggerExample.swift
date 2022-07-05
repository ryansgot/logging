import Foundation
import fslogging

class SwiftDevMetricsLoggerExample: FSDevMetricsLogger {
    func alarm(t: KotlinThrowable, attrs: [String : String]) {
        print("[printdevmetrics] alarm(t=\(t); attrs=\(attrs))")
    }
    
    func info(msg: String, attrs: [String : String]) {
        print("[printdevmetrics] info(msg='\(msg)'; attrs=\(attrs))")
    }
    
    func metric(operationName: String, durationNanos: Int64) {
        print("[printdevmetrics] operationName(msg='\(operationName)'; durationNanos=\(durationNanos))")
    }
    
    func watch(msg: String, attrs: [String : String]) {
        print("[printdevmetrics] watch(msg='\(msg)'; attrs=\(attrs))")
    }
    
    func id() -> String {
        return "printdevmetrics"
    }
    
    func runInTestEnvironment() -> Bool {
        return false
    }
}
