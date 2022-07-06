import Foundation
import UIKit
import fslogging

class IosTestAppDelegate: NSObject, UIApplicationDelegate {
    func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey : Any]? = nil) -> Bool {
        FSEventLog.shared.addLogger(logger: SwiftEventLoggerExample())
        FSEventLog.shared.addLogger(logger: NSLogEventLogger())
        FSDevMetrics.shared.addLogger(logger: SwiftDevMetricsLoggerExample())
        FSDevMetrics.shared.addLogger(logger: NSLogDevMetricsLogger())
        return true
    }
}
